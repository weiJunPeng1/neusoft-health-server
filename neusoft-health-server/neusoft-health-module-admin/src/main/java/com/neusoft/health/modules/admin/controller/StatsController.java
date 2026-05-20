package com.neusoft.health.modules.admin.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.modules.admin.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 数据统计控制器
 * <p>
 * 提供仪表盘统计数据的获取功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Tag(name = "数据统计", description = "管理员数据统计接口，提供系统运营数据的仪表盘概览")
@RestController
@RequestMapping("/api/v1/admin/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    /**
     * 获取仪表盘数据
     *
     * @return 统计数据
     */
    @Operation(summary = "获取仪表盘数据", description = "获取系统运营仪表盘统计数据，包括用户数、咨询量、审核情况等关键指标")
    @GetMapping("/dashboard")
    public R<Map<String, Object>> dashboard() {
        return R.ok(statsService.getDashboardStats());
    }
}
