package com.neusoft.health.modules.security.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PermUpdateDTO {

    @NotNull(message = "旧ID不能为空")
    private Long oldId;

    @NotNull(message = "新ID不能为空")
    private Long newId;
}
