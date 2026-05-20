package com.neusoft.health.modules.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefundApplyDTO {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    @NotBlank(message = "退款原因不能为空")
    private String reason;
}
