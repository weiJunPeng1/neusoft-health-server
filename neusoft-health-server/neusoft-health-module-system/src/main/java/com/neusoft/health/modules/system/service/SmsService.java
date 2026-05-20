package com.neusoft.health.modules.system.service;

import com.neusoft.health.modules.system.dto.SmsCodeSendDTO;

/**
 * 短信服务
 * <p>
 * 提供短信验证码发送和验证功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface SmsService {

    /**
     * 发送短信验证码
     *
     * @param dto      发送验证码请求
     * @param clientIp 客户端IP
     */
    void sendVerificationCode(SmsCodeSendDTO dto, String clientIp);

    /**
     * 验证短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 验证结果，true表示验证成功
     */
    boolean verifyCode(String phone, String code);
}
