package com.neusoft.health.modules.admin.service;

import java.util.List;
import java.util.Map;

public interface StatsService {

    Map<String, Object> getDashboardStats();

    List<Map<String, Object>> getCategoryStats();

    List<Map<String, Object>> getUserTrend(int days);

    List<Map<String, Object>> getConsultTrend(int days);

    Map<String, Object> getMemberStats();

    List<Map<String, Object>> getHotConsults(int limit);

    List<Map<String, Object>> getRecentConsults(int limit);

    Map<String, Object> getAiPerformanceStats();
}
