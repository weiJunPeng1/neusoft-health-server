package com.neusoft.health.modules.consultation.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.framework.util.SecurityUtil;
import com.neusoft.health.modules.consultation.dto.AssistantQueryDTO;
import com.neusoft.health.modules.consultation.service.AssistantService;
import com.neusoft.health.modules.consultation.vo.AssistantResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI健康助手")
@RestController
@RequestMapping("/api/v1/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;

    @Operation(summary = "AI健康助手查询", description = "根据功能类型调用AI健康助手，支持每日健康小贴士、健康评估、生活方式建议")
    @PostMapping("/query")
    public R<AssistantResponseVO> query(@Valid @RequestBody AssistantQueryDTO dto) {
        SecurityUtil.requireCurrentUserId();
        return R.ok(assistantService.query(dto.getFeature(), dto.getHealthProfile()));
    }
}