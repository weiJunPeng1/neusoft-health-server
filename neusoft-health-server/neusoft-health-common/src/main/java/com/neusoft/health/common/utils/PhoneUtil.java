package com.neusoft.health.common.utils;

import cn.hutool.crypto.SecureUtil;

/**
 * 手机号处理工具类
 * <p>
 * 提供手机号的SHA-256哈希计算和脱敏处理功能。
 * 哈希用于唯一索引查找，脱敏用于界面展示。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public final class PhoneUtil {

    /**
     * 私有构造方法，防止实例化
     */
    private PhoneUtil() {
    }

    /**
     * 计算手机号的SHA-256哈希值
     *
     * @param phone 手机号
     * @return SHA-256哈希值
     */
    public static String hash(String phone) {
        return SecureUtil.sha256(phone);
    }

    /**
     * 手机号脱敏处理
     * <p>
     * 将中间4位替换为*，如138****8888
     * </p>
     *
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    public static String mask(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "******" + phone.substring(phone.length() - 2);
    }
}
