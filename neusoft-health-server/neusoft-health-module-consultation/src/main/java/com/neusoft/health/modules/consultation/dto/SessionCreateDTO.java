package com.neusoft.health.modules.consultation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SessionCreateDTO {

    @NotBlank(message = "首条消息不能为空")
    @Size(max = 500, message = "消息长度不能超过500字符")
    private String firstMessage;
}
