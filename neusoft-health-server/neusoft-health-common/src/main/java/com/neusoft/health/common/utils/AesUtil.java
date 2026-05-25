package com.neusoft.health.common.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

import java.nio.charset.StandardCharsets;

/**
 * AES加密工具类
 * <p>
 * 用于敏感信息的对称加密和解密，如手机号等。
 * 使用固定密钥进行AES加密，Base64编码输出。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public final class AesUtil {

    /**
     * AES加密器实例
     */
    private static final AES AES = SecureUtil.aes("neusoft-health-aes-secret-key!!!".getBytes(StandardCharsets.UTF_8));

    /**
     * 私有构造方法，防止实例化
     */
    private AesUtil() {
    }

    /**
     * 加密明文
     *
     * @param plainText 明文
     * @return Base64编码的密文
     */
    public static String encrypt(String plainText) {
        return AES.encryptBase64(plainText);
    }

    /**
     * 解密密文
     *
     * @param cipherText Base64编码的密文
     * @return 明文
     */
    public static String decrypt(String cipherText) {
        return AES.decryptStr(cipherText);
    }
}
