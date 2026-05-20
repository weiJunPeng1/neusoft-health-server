package com.neusoft.health.framework.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 * <p>
 * 提供获取当前登录用户ID等安全相关的工具方法。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public final class SecurityUtil {

    /**
     * 私有构造方法，防止实例化
     */
    private SecurityUtil() {
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID，未登录返回null
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long userId) {
            return userId;
        }
        return null;
    }

    /**
     * 要求当前已登录，返回用户ID
     *
     * @return 用户ID
     * @throws RuntimeException 未登录或登录已过期
     */
    public static Long requireCurrentUserId() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("未登录或登录已过期");
        }
        return userId;
    }
}
