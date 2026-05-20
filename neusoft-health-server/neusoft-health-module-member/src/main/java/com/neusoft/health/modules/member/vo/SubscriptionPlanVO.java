package com.neusoft.health.modules.member.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SubscriptionPlanVO {
    private Long id;
    private String planCode;
    private String planName;
    private String levelCode;
    private String levelName;
    private Integer durationDays;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Boolean isFirstPurchasePrice;
}