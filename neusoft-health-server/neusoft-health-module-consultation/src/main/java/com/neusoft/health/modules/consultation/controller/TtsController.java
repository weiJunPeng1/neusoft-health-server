package com.neusoft.health.modules.consultation.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.framework.ai.TtsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

@Tag(name = "语音合成", description = "TTS语音合成相关接口")
@RestController
@RequestMapping("/api/v1/tts")
@RequiredArgsConstructor
public class TtsController {

    private final TtsService ttsService;

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

    @Operation(summary = "文本转语音（返回音频流）")
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
}
