package com.neusoft.health.modules.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefundApplyDTO {
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    @NotBlank(message = "退款原因不能为空")
    private String reason;
}
