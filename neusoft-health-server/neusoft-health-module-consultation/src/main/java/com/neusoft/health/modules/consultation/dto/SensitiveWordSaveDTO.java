package com.neusoft.health.modules.consultation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SensitiveWordSaveDTO {

    private Long id;

    @NotBlank(message = "敏感词不能为空")
    @Size(max = 128, message = "敏感词最长128个字符")
    private String word;

    private String category;

    private Integer status;
}
