package com.neusoft.health.modules.consultation.dto;

import com.neusoft.health.common.dto.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SensitiveWordQueryDTO extends PageQueryDTO {

    private String keyword;

    private String category;

    private Integer status;
}
