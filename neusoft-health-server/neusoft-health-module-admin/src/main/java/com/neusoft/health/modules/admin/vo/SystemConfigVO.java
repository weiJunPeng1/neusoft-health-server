package com.neusoft.health.modules.admin.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统配置VO
 * <p>
 * 用于展示系统配置信息
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
public class SystemConfigVO {

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 值类型：string=字符串，number=数字，json=JSON，encrypted=加密存储
     */
    private String configType;

    /**
     * 配置说明
     */
    private String description;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
