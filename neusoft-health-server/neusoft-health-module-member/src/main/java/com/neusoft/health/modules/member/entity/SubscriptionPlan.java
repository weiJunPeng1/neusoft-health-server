package com.neusoft.health.modules.member.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("subscription_plans")
public class SubscriptionPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String planCode;
    private String planName;
    private String levelCode;
    private Integer durationDays;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer sortOrder;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    @TableLogic
    private Integer isDeleted;
}