package com.neusoft.health.modules.member.service;

/**
 * 配额服务接口
 */
public interface QuotaService {

    /**
     * 检查并扣减每日咨询配额
     * @param userId 用户ID
     * @param dailyQuota 每日配额（0表示无限）
     * @return true=配额充足，false=配额已用完
     */
    boolean checkAndDecreaseQuota(Long userId, int dailyQuota);

    /**
     * 获取今日剩余配额
     * @param userId 用户ID
     * @param dailyQuota 每日配额（0表示无限）
     * @return 剩余配额数（-1表示无限）
     */
    int getRemainingQuota(Long userId, int dailyQuota);
}