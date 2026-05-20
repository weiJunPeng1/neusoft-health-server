package com.neusoft.health.modules.consultation.dto;

import com.neusoft.health.common.dto.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmergencyLogQueryDTO extends PageQueryDTO {

    private Long userId;

    private String startTime;

    private String endTime;
}
