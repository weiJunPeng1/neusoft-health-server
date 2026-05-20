package com.neusoft.health.modules.consultation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageSendDTO {

    private Long sessionId;

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 500, message = "消息内容最长500字")
    private String content;
}
