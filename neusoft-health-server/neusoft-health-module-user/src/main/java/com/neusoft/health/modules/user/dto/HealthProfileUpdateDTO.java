package com.neusoft.health.modules.user.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HealthProfileUpdateDTO {

    private BigDecimal height;

    private BigDecimal weight;

    private String bloodType;

    private String allergies;

    private String medicalHistory;

    private String medicationHistory;

    private String familyHistory;

    private Integer autoSync;
}
