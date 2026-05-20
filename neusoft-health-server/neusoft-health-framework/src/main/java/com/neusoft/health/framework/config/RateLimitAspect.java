package com.neusoft.health.framework.config;

import com.neusoft.health.common.exception.BusinessException;
import com.neusoft.health.framework.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 限流切面
 * <p>
 * 使用Redis实现用户咨询频率限制，防止恶意刷屏。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    /**
     * 字符串Redis模板
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 限流Redis键前缀
     */
    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:consultation:";
    /**
     * 每分钟最大请求数
     */
    private static final long MAX_REQUESTS_PER_MINUTE = 60;
    /**
     * 时间窗口大小（秒）
     */
    private static final long WINDOW_DURATION_SECONDS = 60;

    /**
     * 检查咨询频率限制
     *
     * @param joinPoint 连接点
     * @return 原方法执行结果
     * @throws Throwable 异常
     */
    @Around("execution(* com.neusoft.health.modules.consultation.controller.ConsultationController.sendMessage(..))")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return joinPoint.proceed();
        }

        String key = RATE_LIMIT_KEY_PREFIX + userId;
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count == null) {
            count = 1L;
        }

        if (count == 1) {
            stringRedisTemplate.expire(key, WINDOW_DURATION_SECONDS, TimeUnit.SECONDS);
        }

        if (count > MAX_REQUESTS_PER_MINUTE) {
            log.warn("用户 {} 咨询频率超限，当前次数: {}", userId, count);
            throw new BusinessException(429, "咨询过于频繁，请稍后再试");
        }

        return joinPoint.proceed();
    }
}
