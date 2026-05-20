package com.neusoft.health.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置表（system_configs）
 * <p>
 * 存储系统全局配置参数，以Key-Value形式管理。
 * 敏感配置（如API密钥）config_type为encrypted，存储前AES加密。
 * 配置修改实时生效（通过Redis缓存通知机制），修改记录同步写入operation_logs。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("system_configs")
public class SystemConfig {

    /** 配置ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 配置键（如deepseek.api.key、sms.code.expire.minutes） */
    private String configKey;

    /** 配置值 */
    private String configValue;

    /** 值类型：string=字符串，number=数字，json=JSON，encrypted=加密存储 */
    private String configType;

    /** 配置说明描述 */
    private String description;

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
