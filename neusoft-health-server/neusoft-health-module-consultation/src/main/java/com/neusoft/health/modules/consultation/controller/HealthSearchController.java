package com.neusoft.health.modules.consultation.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.modules.consultation.dto.HealthSearchDTO;
import com.neusoft.health.modules.consultation.service.HealthSearchService;
import com.neusoft.health.modules.consultation.vo.HealthSearchVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "健康搜索", description = "健康知识搜索接口，优先从数据库缓存获取，未命中时调用AI生成并缓存")
@RestController
@RequestMapping("/api/v1/health-search")
@RequiredArgsConstructor
public class HealthSearchController {

    private final HealthSearchService healthSearchService;

    @Operation(summary = "健康知识搜索", description = "根据关键词搜索健康知识，优先使用数据库缓存，未命中时由AI生成并缓存结果")
    @PostMapping
    public R<HealthSearchVO> search(@Valid @RequestBody HealthSearchDTO dto) {
        return R.ok(healthSearchService.search(dto.getKeyword()));
    }
}
