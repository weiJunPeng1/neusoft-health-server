package com.neusoft.health.modules.consultation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HealthSearchDTO {

    @NotBlank(message = "搜索关键词不能为空")
    @Size(max = 200, message = "搜索关键词最长200字")
    private String keyword;
}
