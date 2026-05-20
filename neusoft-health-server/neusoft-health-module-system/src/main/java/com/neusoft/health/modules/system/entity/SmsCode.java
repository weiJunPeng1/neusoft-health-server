package com.neusoft.health.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 短信验证码表（sms_codes）
 * <p>
 * 存储短信登录验证码记录。通过 phone_hash 关联用户手机号，支持有效期校验和发送频率控制。
 * 验证码有效期由系统配置 sms.code.expire.minutes 决定（默认5分钟）。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("sms_codes")
public class SmsCode {

    /** 验证码ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 手机号SHA-256哈希值（关联users.phone_hash） */
    private String phoneHash;

    /** 6位数字验证码 */
    private String code;

    /** 过期时间（超过此时间后验证码失效） */
    private LocalDateTime expireTime;

    /** 是否已使用：0=未使用，1=已使用 */
    private Integer used;

    /** 发送时客户端IP地址 */
    private String sendIp;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 逻辑删除：0=否，1=是 */
    @TableLogic
    private Integer isDeleted;
}
