package com.neusoft.health.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户设置表（user_settings）
 * <p>
 * 存储用户个性化的系统设置参数，包括通知开关、语音播报配置、匿名咨询模式等。
 * 与 users 表一对一关联（user_id 唯一索引），创建用户时自动初始化默认值。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("user_settings")
public class UserSetting {

    /** 设置ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（关联users.id，唯一） */
    private Long userId;

    /** 通知开关：0=关闭，1=开启 */
    private Integer notificationEnabled;

    /** 语音播报语速（默认1.00，范围0.50-2.00） */
    private BigDecimal voiceSpeed;

    /** 语音音量（默认80，范围0-100） */
    private Integer voiceVolume;

    /** 语音音色（default/male/female/child） */
    private String voiceTone;

    /** 匿名咨询模式：0=关闭，1=开启 */
    private Integer anonymousMode;

    /** 是否允许推荐健康科普：0=否，1=是 */
    private Integer recommendEnabled;

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
