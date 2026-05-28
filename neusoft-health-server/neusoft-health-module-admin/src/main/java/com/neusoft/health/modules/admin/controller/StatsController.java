package com.neusoft.health.modules.admin.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.modules.admin.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "数据统计", description = "管理员数据统计接口，提供系统运营数据的仪表盘概览")
@RestController
@RequestMapping("/api/v1/admin/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "获取仪表盘数据", description = "获取系统运营仪表盘统计数据，包括用户数、咨询量、审核情况等关键指标")
    @GetMapping("/dashboard")
    public R<Map<String, Object>> dashboard() {
        return R.ok(statsService.getDashboardStats());
    }

    @Operation(summary = "获取咨询分类统计", description = "获取各咨询分类的会话数量统计")
    @GetMapping("/categories")
    public R<List<Map<String, Object>>> categoryStats() {
        return R.ok(statsService.getCategoryStats());
    }

    @Operation(summary = "获取用户增长趋势", description = "获取指定天数内的用户增长趋势数据")
    @GetMapping("/user-trend")
    public R<List<Map<String, Object>>> userTrend(@RequestParam(defaultValue = "30") int days) {
        return R.ok(statsService.getUserTrend(days));
    }

    @Operation(summary = "获取咨询量趋势", description = "获取指定天数内的咨询量趋势数据")
    @GetMapping("/consult-trend")
    public R<List<Map<String, Object>>> consultTrend(@RequestParam(defaultValue = "30") int days) {
        return R.ok(statsService.getConsultTrend(days));
    }

    @Operation(summary = "获取会员统计", description = "获取各等级会员数量统计")
    @GetMapping("/member-stats")
    public R<Map<String, Object>> memberStats() {
        return R.ok(statsService.getMemberStats());
    }

    @Operation(summary = "获取热门咨询排行", description = "获取消息数量最多的咨询会话排行")
    @GetMapping("/hot-consults")
    public R<List<Map<String, Object>>> hotConsults(@RequestParam(defaultValue = "8") int limit) {
        return R.ok(statsService.getHotConsults(limit));
    }

    @Operation(summary = "获取最新咨询动态", description = "获取最新的用户咨询消息列表")
    @GetMapping("/recent-consults")
    public R<List<Map<String, Object>>> recentConsults(@RequestParam(defaultValue = "10") int limit) {
        return R.ok(statsService.getRecentConsults(limit));
    }

    @Operation(summary = "获取AI性能统计", description = "获取AI响应时间等性能指标统计")
    @GetMapping("/ai-performance")
    public R<Map<String, Object>> aiPerformance() {
        return R.ok(statsService.getAiPerformanceStats());
    }
}
