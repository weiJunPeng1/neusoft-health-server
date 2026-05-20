package com.neusoft.health.modules.consultation.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmergencyLogVO {

    private Long id;

    private Long userId;

    private Long messageId;

    private String keywordMatched;

    private Integer handled;

    private LocalDateTime createdTime;
}
