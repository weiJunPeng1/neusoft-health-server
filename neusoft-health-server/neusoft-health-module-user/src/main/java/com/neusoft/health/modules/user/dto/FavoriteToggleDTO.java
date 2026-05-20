package com.neusoft.health.modules.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteToggleDTO {

    @NotNull(message = "消息ID不能为空")
    private Long messageId;
}
