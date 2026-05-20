package com.neusoft.health.modules.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户状态DTO
 * <p>
 * 用于切换用户启用/禁用状态
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
public class UserStatusDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 操作原因
     */
    @NotBlank(message = "操作原因不能为空")
    private String reason;
}
