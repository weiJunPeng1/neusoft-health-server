package com.neusoft.health.modules.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserHealthProfileUpdateDTO {

    private BigDecimal height;

    private BigDecimal weight;

    private String bloodType;

    @Size(max = 500, message = "过敏史最长500字")
    private String allergies;

    @Size(max = 500, message = "既往病史最长500字")
    private String medicalHistory;

    @Size(max = 500, message = "用药史最长500字")
    private String medicationHistory;

    @Size(max = 500, message = "家族病史最长500字")
    private String familyHistory;

    private Integer autoSync;
}
