package com.neusoft.health.modules.consultation.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.modules.consultation.dto.SensitiveWordCheckDTO;
import com.neusoft.health.modules.consultation.service.SensitiveWordService;
import com.neusoft.health.modules.consultation.vo.SensitiveWordCheckVO;
import com.neusoft.health.modules.consultation.vo.SensitiveWordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "敏感词检测", description = "内容安全敏感词检测相关接口，用于消息内容的安全过滤")
@RestController
@RequestMapping("/api/v1/sensitive-word")
@RequiredArgsConstructor
public class SensitiveWordController {

    private final SensitiveWordService sensitiveWordService;

    @Operation(summary = "获取敏感词列表", description = "获取系统配置的所有敏感词列表，包含分类和严重级别信息")
    @GetMapping("/list")
    public R<List<SensitiveWordVO>> listWords() {
        return R.ok(sensitiveWordService.listWords());
    }

    @Operation(summary = "检测文本内容", description = "对输入文本进行敏感词检测，返回是否命中及命中的敏感词列表")
    @PostMapping("/check")
    public R<SensitiveWordCheckVO> check(@RequestBody SensitiveWordCheckDTO dto) {
        return R.ok(sensitiveWordService.check(dto.getContent()));
    }
}
