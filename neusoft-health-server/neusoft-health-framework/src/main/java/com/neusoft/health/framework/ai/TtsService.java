package com.neusoft.health.framework.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neusoft.health.framework.config.TtsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class TtsService {

    private final TtsProperties ttsProperties;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public byte[] synthesize(String text) {
        try {
            String cleanText = text.replaceAll("<[^>]+>", "").replaceAll("\\s+", " ").trim();
            String authUrl = buildAuthUrl();
            String appId = ttsProperties.getAppId();

            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<byte[]> audioRef = new AtomicReference<>();
            ByteArrayOutputStream audioBuffer = new ByteArrayOutputStream();

            Request request = new Request.Builder().url(authUrl).build();

            okHttpClient.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    try {
                        Map<String, Object> payload = buildRequest(appId, cleanText);
                        String json = objectMapper.writeValueAsString(payload);
                        webSocket.send(json);
                    } catch (Exception e) {
                        log.error("发送TTS请求失败", e);
                        latch.countDown();
                    }
                }

                @Override
                public void onMessage(WebSocket webSocket, String msg) {
                    try {
                        Map<String, Object> resp = objectMapper.readValue(msg, Map.class);
                        Map<String, Object> header = (Map<String, Object>) resp.get("header");
                        int status = (int) header.get("status");
                        int code = (int) header.get("code");

                        if (code != 0) {
                            log.error("TTS合成错误: code={}, message={}", code, header.get("message"));
                            latch.countDown();
                            return;
                        }

                        Map<String, Object> payload = (Map<String, Object>) resp.get("payload");
                        if (payload != null) {
                            Map<String, Object> audio = (Map<String, Object>) payload.get("audio");
                            if (audio != null) {
                                String audioBase64 = (String) audio.get("audio");
                                if (audioBase64 != null) {
                                    byte[] chunk = Base64.getDecoder().decode(audioBase64);
                                    audioBuffer.write(chunk);
                                }
                            }
                        }

                        if (status == 2) {
                            audioRef.set(audioBuffer.toByteArray());
                            webSocket.close(1000, "done");
                            latch.countDown();
                        }
                    } catch (Exception e) {
                        log.error("解析TTS响应失败", e);
                        latch.countDown();
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    log.error("TTS WebSocket连接失败", t);
                    latch.countDown();
                }
            });

            boolean finished = latch.await(30, TimeUnit.SECONDS);
            if (!finished) {
                log.error("TTS合成超时");
                return null;
            }

            return audioRef.get();
        } catch (Exception e) {
            log.error("TTS合成异常", e);
            return null;
        }
    }

    private String buildAuthUrl() throws Exception {
        URI uri = URI.create(ttsProperties.getApiUrl());
        String host = uri.getHost();
        String path = uri.getPath();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = sdf.format(new Date());

        String signatureOrigin = String.format("host: %s\ndate: %s\nGET %s HTTP/1.1", host, date, path);

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(ttsProperties.getApiSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(signatureOrigin.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(signData);

        String authorizationOrigin = String.format(
                "api_key=\"%s\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"%s\"",
                ttsProperties.getApiKey(), signature);
        String authorization = Base64.getEncoder().encodeToString(
                authorizationOrigin.getBytes(StandardCharsets.UTF_8));

        return ttsProperties.getApiUrl() +
                "?authorization=" + authorization +
                "&date=" + date +
                "&host=" + host;
    }

    private Map<String, Object> buildRequest(String appId, String text) {
        Map<String, Object> header = Map.of(
                "app_id", appId,
                "status", 2
        );

        Map<String, Object> audio = Map.of(
                "encoding", "lame",
                "sample_rate", 24000,
                "channels", 1,
                "bit_depth", 16,
                "frame_size", 0
        );

        Map<String, Object> tts = Map.of(
                "vcn", ttsProperties.getVoice(),
                "speed", ttsProperties.getSpeed(),
                "volume", ttsProperties.getVolume(),
                "pitch", ttsProperties.getPitch(),
                "bgs", 0,
                "reg", 0,
                "rdn", 0,
                "rhy", 0,
                "audio", audio
        );

        Map<String, Object> parameter = Map.of("tts", tts);

        Map<String, Object> textPayload = Map.of(
                "encoding", "utf8",
                "compress", "raw",
                "format", "plain",
                "status", 2,
                "seq", 0,
                "text", Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8))
        );

        Map<String, Object> payload = Map.of("text", textPayload);

        return Map.of(
                "header", header,
                "parameter", parameter,
                "payload", payload
        );
    }
}
