package com.neusoft.health.framework.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neusoft.health.framework.ai.protocol.SpeechMessage;
import com.neusoft.health.framework.ai.protocol.SpeechProtocol.EventType;
import com.neusoft.health.framework.ai.protocol.SpeechProtocol.MsgType;
import com.neusoft.health.framework.ai.protocol.SpeechProtocol.MsgTypeFlagBits;
import com.neusoft.health.framework.config.TtsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class TtsService {

    private final TtsProperties ttsProperties;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String DOUBAO_WS_URL = "wss://openspeech.bytedance.com/api/v3/tts/unidirectional/stream";

    public byte[] synthesize(String text) {
        return synthesizeDoubao(text);
    }

    public void synthesizeStream(String text, Consumer<byte[]> onChunk, Runnable onComplete) {
        synthesizeDoubaoStream(text, onChunk, onComplete);
    }

    private byte[] synthesizeDoubao(String text) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<byte[]> result = new AtomicReference<>();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        synthesizeDoubaoStream(text, chunk -> {
            try {
                buffer.write(chunk);
            } catch (Exception e) {
                log.error("豆包TTS写入缓冲区失败", e);
            }
        }, () -> {
            result.set(buffer.toByteArray());
            latch.countDown();
        });

        try {
            boolean done = latch.await(30, TimeUnit.SECONDS);
            if (!done) {
                log.error("豆包TTS合成超时");
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        return result.get();
    }

    private void synthesizeDoubaoStream(String text, Consumer<byte[]> onChunk, Runnable onComplete) {
        String cleanText = text.replaceAll("<[^>]+>", "").replaceAll("\\s+", " ").trim();
        String connectId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();

        log.info("豆包TTS V3请求: resourceId={}, speaker={}, connectId={}", ttsProperties.getResourceId(), ttsProperties.getSpeaker(), connectId);

        Request.Builder requestBuilder = new Request.Builder()
                .url(DOUBAO_WS_URL)
                .addHeader("X-Api-Resource-Id", ttsProperties.getResourceId())
                .addHeader("X-Api-Request-Id", requestId)
                .addHeader("X-Api-Connect-Id", connectId);

        if (ttsProperties.getApiKey() != null && !ttsProperties.getApiKey().isBlank()) {
            requestBuilder.addHeader("X-Api-Key", ttsProperties.getApiKey());
            log.info("豆包TTS鉴权: X-Api-Key={}, X-Api-Resource-Id={}", mask(ttsProperties.getApiKey()), ttsProperties.getResourceId());
        } else {
            requestBuilder.addHeader("X-Api-App-Id", ttsProperties.getAppId())
                    .addHeader("X-Api-Access-Key", ttsProperties.getAccessToken());
            log.info("豆包TTS鉴权: X-Api-App-Id={}, X-Api-Access-Key={}", ttsProperties.getAppId(), mask(ttsProperties.getAccessToken()));
        }

        Request request = requestBuilder.build();

        CountDownLatch openLatch = new CountDownLatch(1);
        AtomicReference<WebSocket> wsRef = new AtomicReference<>();

        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                log.debug("豆包TTS V3 WebSocket建连成功, connectId={}", connectId);
                wsRef.set(webSocket);
                openLatch.countDown();
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                try {
                    SpeechMessage msg = SpeechMessage.unmarshal(bytes.toByteArray());

                    if (msg.getType() == MsgType.AUDIO_ONLY_SERVER) {
                        if (msg.getPayload() != null && msg.getPayload().length > 0) {
                            onChunk.accept(msg.getPayload());
                        }
                    } else if (msg.getType() == MsgType.ERROR) {
                        String errMsg = msg.getPayload() != null
                                ? new String(msg.getPayload(), StandardCharsets.UTF_8) : "unknown";
                        log.error("豆包TTS V3服务端错误: errorCode={}, payload={}", msg.getErrorCode(), errMsg);
                        webSocket.close(1011, "server error");
                        onComplete.run();
                    } else if (msg.getType() == MsgType.FULL_SERVER_RESPONSE
                            && msg.getEvent() == EventType.SESSION_FINISHED) {
                        log.info("豆包TTS V3合成完成(SessionFinished), connectId={}, 发送FinishConnection", connectId);
                        try {
                            SpeechMessage finishMsg = new SpeechMessage(MsgType.FULL_CLIENT_REQUEST, MsgTypeFlagBits.WITH_EVENT);
                            finishMsg.setEvent(EventType.FINISH_CONNECTION);
                            finishMsg.setPayload("{}".getBytes(StandardCharsets.UTF_8));
                            webSocket.send(ByteString.of(finishMsg.marshal()));
                        } catch (Exception e) {
                            log.warn("豆包TTS V3发送FinishConnection失败", e);
                            webSocket.close(1000, "done");
                            onComplete.run();
                        }
                    } else if (msg.getType() == MsgType.FULL_SERVER_RESPONSE
                            && msg.getEvent() == EventType.CONNECTION_FINISHED) {
                        log.info("豆包TTS V3连接已正常关闭(ConnectionFinished), connectId={}", connectId);
                        webSocket.close(1000, "done");
                        onComplete.run();
                    } else if (msg.getType() == MsgType.FULL_SERVER_RESPONSE) {
                        log.debug("豆包TTS V3收到服务端响应: event={}, connectId={}", msg.getEvent(), connectId);
                    }
                } catch (Exception e) {
                    log.error("豆包TTS V3解析二进制消息失败: connectId={}", connectId, e);
                    onComplete.run();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                log.warn("豆包TTS V3收到意外的文本消息: {}", text);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                log.error("豆包TTS V3 WebSocket连接失败: connectId={}", connectId, t);
                openLatch.countDown();
                onComplete.run();
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                log.debug("豆包TTS V3 WebSocket已关闭: code={}, reason={}", code, reason);
            }
        };

        WebSocket ws = okHttpClient.newWebSocket(request, listener);

        try {
            boolean opened = openLatch.await(10, TimeUnit.SECONDS);
            if (!opened || wsRef.get() == null) {
                log.error("豆包TTS V3 WebSocket建连超时: connectId={}", connectId);
                onComplete.run();
                return;
            }

            try {
                Map<String, Object> requestPayload = buildDoubaoPayload(cleanText);
                String payloadJson = objectMapper.writeValueAsString(requestPayload);

                SpeechMessage fullMsg = new SpeechMessage(MsgType.FULL_CLIENT_REQUEST, MsgTypeFlagBits.NO_SEQ);
                fullMsg.setPayload(payloadJson.getBytes(StandardCharsets.UTF_8));
                byte[] marshaled = fullMsg.marshal();
                ws.send(ByteString.of(marshaled));
                log.debug("豆包TTS V3文本已发送, connectId={}, textLength={}", connectId, cleanText.length());
            } catch (Exception e) {
                log.error("豆包TTS V3发送合成请求失败: connectId={}", connectId, e);
                onComplete.run();
            }
        } catch (Exception e) {
            log.error("豆包TTS V3流程异常: connectId={}", connectId, e);
            onComplete.run();
        }
    }

    private static String mask(String value) {
        if (value == null) {
            return "null";
        }
        return value.substring(0, Math.min(8, value.length())) + "***";
    }

    private Map<String, Object> buildDoubaoPayload(String text) {
        Map<String, Object> audioParams = new LinkedHashMap<>();
        audioParams.put("format", ttsProperties.getFormat());
        audioParams.put("sample_rate", ttsProperties.getSampleRate());

        Map<String, Object> reqParams = new LinkedHashMap<>();
        reqParams.put("speaker", ttsProperties.getSpeaker());
        reqParams.put("text", text);
        reqParams.put("audio_params", audioParams);

        Map<String, Object> additions = new LinkedHashMap<>();
        additions.put("disable_markdown_filter", false);
        try {
            reqParams.put("additions", objectMapper.writeValueAsString(additions));
        } catch (Exception e) {
            reqParams.put("additions", "{\"disable_markdown_filter\":false}");
        }

        Map<String, Object> user = Map.of("uid", UUID.randomUUID().toString());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("user", user);
        payload.put("req_params", reqParams);
        return payload;
    }
}
