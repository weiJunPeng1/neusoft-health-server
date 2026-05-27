package com.neusoft.health.modules.consultation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AssistantQueryDTO {

    @NotBlank(message = "功能类型不能为空")
    @Pattern(regexp = "daily_tips|health_assessment|lifestyle_advice", message = "功能类型必须是 daily_tips、health_assessment 或 lifestyle_advice")
    private String feature;

    private String healthProfile;
}