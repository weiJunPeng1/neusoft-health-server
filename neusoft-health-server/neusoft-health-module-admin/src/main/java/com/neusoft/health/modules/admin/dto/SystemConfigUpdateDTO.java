package com.neusoft.health.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 系统配置更新DTO
 * <p>
 * 用于更新系统配置
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
public class SystemConfigUpdateDTO {

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 配置键
     */
    @NotBlank(message = "配置键不能为空")
    @Size(max = 64, message = "配置键最长64字符")
    private String configKey;

    /**
     * 配置值
     */
    @NotBlank(message = "配置值不能为空")
    private String configValue;

    /**
     * 配置说明
     */
    private String description;
}
