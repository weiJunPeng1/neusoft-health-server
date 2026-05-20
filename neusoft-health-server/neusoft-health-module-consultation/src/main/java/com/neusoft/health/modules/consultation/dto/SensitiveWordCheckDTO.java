package com.neusoft.health.modules.consultation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SensitiveWordCheckDTO {

    @NotBlank(message = "检测内容不能为空")
    private String content;
}
