package com.neusoft.health.modules.system.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户详情VO
 * <p>
 * 用于展示用户详细信息
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
public class UserDetailVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 手机号（已脱敏）
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 性别：0=未知，1=男，2=女
     */
    private Integer gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 注册方式：sms=短信验证码，wechat=微信，alipay=支付宝
     */
    private String loginType;

    /**
     * 是否已确认免责声明：0=未确认，1=已确认
     */
    private Integer disclaimerAccepted;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 状态：0=禁用，1=正常
     */
    private Integer status;

    /**
     * 咨询总次数
     */
    private Integer totalConsultations;

    /**
     * 消息总条数
     */
    private Integer totalMessages;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}
