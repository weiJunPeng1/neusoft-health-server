package com.neusoft.health.modules.system.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户登录日志VO
 * <p>
 * 用于展示用户登录日志信息
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
public class UserLoginLogVO {

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录方式：sms=短信验证码，wechat=微信，alipay=支付宝，pwd=密码
     */
    private String loginType;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 用户代理信息
     */
    private String userAgent;

    /**
     * 登录结果：0=失败，1=成功
     */
    private Integer loginResult;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 创建时间（登录时间）
     */
    private LocalDateTime createdTime;
}
