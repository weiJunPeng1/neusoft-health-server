package com.neusoft.health.modules.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminGrantDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @NotBlank(message = "等级不能为空")
    private String levelCode;
    @NotNull(message = "天数不能为空")
    private Integer durationDays;
    private String reason;
}