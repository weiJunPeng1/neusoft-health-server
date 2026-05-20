package com.neusoft.health.modules.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreateDTO {
    @NotNull(message = "方案ID不能为空")
    private Long planId;
}