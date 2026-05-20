package com.neusoft.health.modules.consultation.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDetailVO {

    private Long id;

    private Long sessionId;

    private Long userId;

    private String role;

    private String content;

    private Integer contentType;

    private Integer isEmergency;

    private String emergencyKeyword;

    private Integer isViolation;

    private String violationReason;

    private Integer reviewStatus;

    private Long reviewedBy;

    private String reviewedByName;

    private LocalDateTime reviewedAt;

    private String modifiedContent;

    private Integer apiCallDuration;

    private LocalDateTime createdTime;
}
