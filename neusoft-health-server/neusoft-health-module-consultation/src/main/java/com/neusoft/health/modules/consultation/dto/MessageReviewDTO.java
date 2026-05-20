package com.neusoft.health.modules.consultation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageReviewDTO {

    @NotNull(message = "消息ID不能为空")
    private Long messageId;

    @NotNull(message = "审核状态不能为空")
    private Integer reviewStatus;

    private String violationReason;

    private String modifiedContent;
}
