package com.neusoft.health.modules.member.service.impl;

import com.neusoft.health.modules.member.service.QuotaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 配额服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuotaServiceImpl implements QuotaService {

    private final StringRedisTemplate redisTemplate;

    private static final String QUOTA_KEY_PREFIX = "health:quota:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public boolean checkAndDecreaseQuota(Long userId, int dailyQuota) {
        if (dailyQuota == 0) {
            return true;
        }

        String key = getQuotaKey(userId);
        String current = redisTemplate.opsForValue().get(key);
        
        int used = current != null ? Integer.parseInt(current) : 0;
        
        if (used >= dailyQuota) {
            log.warn("User {} exceeded daily quota: used={}, limit={}", userId, used, dailyQuota);
            return false;
        }

        Long result = redisTemplate.opsForValue().increment(key);
        if (result != null && result == 1) {
            redisTemplate.expire(key, 1, java.util.concurrent.TimeUnit.DAYS);
        }
        
        log.debug("User {} quota check: used={}, limit={}", userId, used + 1, dailyQuota);
        return true;
    }

    @Override
    public int getRemainingQuota(Long userId, int dailyQuota) {
        if (dailyQuota == 0) {
            return -1;
        }

        String key = getQuotaKey(userId);
        String current = redisTemplate.opsForValue().get(key);
        
        int used = current != null ? Integer.parseInt(current) : 0;
        return Math.max(0, dailyQuota - used);
    }

    private String getQuotaKey(Long userId) {
        return QUOTA_KEY_PREFIX + userId + ":" + LocalDate.now().format(DATE_FORMATTER);
    }
}