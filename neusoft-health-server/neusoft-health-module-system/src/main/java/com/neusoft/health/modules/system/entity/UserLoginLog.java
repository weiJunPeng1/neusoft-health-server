package com.neusoft.health.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户登录日志表（user_login_logs）
 * <p>
 * 记录用户每次登录的详细信息，包括登录方式、IP、设备信息、登录结果等。
 * 用于安全审计、异常登录检测和用户行为分析。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("user_login_logs")
public class UserLoginLog {

    /** 日志ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（关联users.id） */
    private Long userId;

    /** 登录方式：sms=短信验证码，wechat=微信，alipay=支付宝，pwd=密码 */
    private String loginType;

    /** 登录IP地址 */
    private String loginIp;

    /** 用户代理信息（User-Agent） */
    private String userAgent;

    /** 登录结果：0=失败，1=成功 */
    private Integer loginResult;

    /** 失败原因（登录失败时记录） */
    private String failReason;

    /** 创建时间（自动填充，即登录时间） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 逻辑删除：0=否，1=是 */
    @TableLogic
    private Integer isDeleted;
}
