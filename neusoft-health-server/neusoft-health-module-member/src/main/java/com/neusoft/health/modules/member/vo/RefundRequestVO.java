package com.neusoft.health.modules.member.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RefundRequestVO {
    private Long id;
    private Long orderId;
    private String orderNo;
    private Long userId;
    private String userPhone;
    private String reason;
    private BigDecimal refundAmount;
    private Integer status;
    private String statusDesc;
    private Long handledBy;
    private String handleRemark;
    private LocalDateTime handledTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
