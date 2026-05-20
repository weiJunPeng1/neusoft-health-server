package com.neusoft.health.modules.user.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserHealthProfileVO {

    private Long id;

    private Long userId;

    private BigDecimal height;

    private BigDecimal weight;

    private String bloodType;

    private String allergies;

    private String medicalHistory;

    private Integer autoSync;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
