package com.neusoft.health.modules.member.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentOrderVO {
    private String orderNo;
    private Long planId;
    private String planName;
    private BigDecimal amount;
    private String payMethod;
    private Integer payStatus;
    private String payStatusDesc;
    private LocalDateTime paidTime;
    private LocalDateTime expireTime;
    private LocalDateTime createdTime;
}