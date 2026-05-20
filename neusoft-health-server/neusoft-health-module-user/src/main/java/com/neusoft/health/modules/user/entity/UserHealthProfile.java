package com.neusoft.health.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户健康档案表（user_health_profiles）
 * <p>
 * 存储用户的基础健康信息，用于AI咨询时提供个性化建议。
 * 过敏史和既往病史字段采用AES加密存储，保护用户隐私。
 * auto_sync 控制是否将健康档案自动同步到AI上下文。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("user_health_profiles")
public class UserHealthProfile {

    /** 档案ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（关联users.id） */
    private Long userId;

    /** 身高（cm，精确到2位小数） */
    private BigDecimal height;

    /** 体重（kg，精确到2位小数） */
    private BigDecimal weight;

    /** 血型：A/B/AB/O/未知 */
    private String bloodType;

    /** 过敏史（AES-256加密存储） */
    private String allergies;

    /** 既往病史（AES-256加密存储） */
    private String medicalHistory;

    /** 是否自动同步给AI：0=否，1=是 */
    private Integer autoSync;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /** 逻辑删除：0=否，1=是 */
    @TableLogic
    private Integer isDeleted;
}
