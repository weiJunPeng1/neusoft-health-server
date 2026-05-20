package com.neusoft.health.modules.admin.service;

import java.util.Map;

/**
 * 统计数据服务
 * <p>
 * 提供仪表盘统计数据的获取功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface StatsService {

    /**
     * 获取仪表盘统计数据
     *
     * @return 统计数据Map
     */
    Map<String, Object> getDashboardStats();
}
