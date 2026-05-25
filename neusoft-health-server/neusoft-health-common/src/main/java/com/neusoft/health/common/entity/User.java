package com.neusoft.health.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表（users）
 * <p>
 * 存储所有用户（普通用户/管理员）的核心信息。
 * 手机号采用双重存储：phone_hash（SHA-256）用于唯一索引查找，phone_encrypted（AES-256）用于解密还原。
 * 第三方快捷登录预留 open_id + login_type 字段。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("users")
public class User {

    /** 用户ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 手机号SHA-256哈希值（用于唯一索引，支持快速登录查找） */
    private String phoneHash;

    /** 手机号AES-256加密存储（解密后可还原明文） */
    private String phoneEncrypted;

    /** BCrypt密码哈希（可选，短信验证码登录时可为null） */
    private String passwordHash;

    /** 昵称（2-20个字符，支持中文/英文/数字/下划线） */
    private String nickname;

    /** 头像URL（最大512字符） */
    private String avatarUrl;

    /** 性别：0=未知，1=男，2=女 */
    private Integer gender;

    /** 年龄（0-120） */
    private Integer age;

    /** 第三方登录OpenID（微信/支付宝快捷登录） */
    private String openId;

    /** 注册方式：sms=短信验证码，wechat=微信，alipay=支付宝 */
    private String loginType;

    /** 是否已确认免责声明：0=未确认，1=已确认 */
    private Integer disclaimerAccepted;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 账号状态：0=禁用，1=正常 */
    private Integer status;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /** 逻辑删除：0=否，1=是 */
    @TableLogic
    private Integer isDeleted;

    /** 当前会员等级：L0/L1/L2/L3 */
    private String memberLevel;

    /** 会员到期时间 */
    private LocalDateTime memberExpireTime;

    /** 邀请码（8位唯一，用于邀请奖励） */
    private String inviteCode;

    /** 邀请人ID（被谁邀请） */
    private Long invitedBy;

    /** 是否已完成首购：0=否，1=是 */
    private Integer firstPurchase;
}
