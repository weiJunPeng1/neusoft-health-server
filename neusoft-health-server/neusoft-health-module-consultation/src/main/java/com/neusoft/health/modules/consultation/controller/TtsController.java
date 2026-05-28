package com.neusoft.health.modules.consultation.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.framework.ai.TtsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Tag(name = "语音合成", description = "TTS语音合成相关接口")
@RestController
@RequestMapping("/api/v1/tts")
@RequiredArgsConstructor
public class TtsController {

    private final TtsService ttsService;
    private final ExecutorService ttsExecutor = Executors.newCachedThreadPool();

    @Operation(summary = "文本转语音")
    @PostMapping("/synthesize")
    public R<Map<String, String>> synthesize(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.isBlank()) {
            return R.fail(400, "文本不能为空");
        }

        byte[] audio = ttsService.synthesize(text);
        if (audio == null) {
            return R.fail(500, "语音合成失败");
        }

        String base64Audio = Base64.getEncoder().encodeToString(audio);
        return R.ok(Map.of(
                "audio", base64Audio,
                "format", "mp3"
        ));
    }

    @Operation(summary = "文本转语音（返回完整音频流）")
    @PostMapping(value = "/synthesize/stream", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> synthesizeStream(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        byte[] audio = ttsService.synthesize(text);
        if (audio == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("audio/mpeg"))
                .body(audio);
    }

    @Operation(summary = "文本转语音（SSE实时流式推送）", description = "音频块到达即刻推送，前端可实现边收边播")
    @PostMapping(value = "/synthesize/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter synthesizeSse(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.isBlank()) {
            SseEmitter emitter = new SseEmitter(5000L);
            try {
                emitter.send(SseEmitter.event().name("error").data("文本不能为空"));
            } catch (IOException e) {
                log.debug("发送空文本错误事件失败", e);
            }
            emitter.complete();
            return emitter;
        }

        SseEmitter emitter = new SseEmitter(60_000L);

        ttsExecutor.execute(() -> {
            try {
                ttsService.synthesizeStream(text,
                        chunk -> {
                            try {
                                String base64 = Base64.getEncoder().encodeToString(chunk);
                                emitter.send(SseEmitter.event()
                                        .name("audio")
                                        .data(base64));
                            } catch (Exception e) {
                                log.warn("SSE推送音频块失败", e);
                            }
                        },
                        () -> {
                            try {
                                emitter.send(SseEmitter.event()
                                        .name("done")
                                        .data("complete"));
                                emitter.complete();
                            } catch (Exception e) {
                                log.debug("SSE发送完成事件异常", e);
                            }
                        }
                );
            } catch (Exception e) {
                log.error("TTS SSE合成异常", e);
                try {
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    log.debug("SSE completeWithError失败", ex);
                }
            }
        });

        emitter.onTimeout(() -> {
            log.warn("TTS SSE连接超时");
            emitter.complete();
        });
        emitter.onError(e -> log.warn("TTS SSE连接错误: {}", e.getMessage()));

        return emitter;
    }
}
