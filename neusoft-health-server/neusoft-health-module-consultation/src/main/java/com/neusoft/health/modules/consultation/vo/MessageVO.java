package com.neusoft.health.modules.consultation.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageVO {

    private Long id;

    private Long sessionId;

    private String role;

    private String content;

    private Integer isEmergency;

    private String disclaimer;

    private LocalDateTime createdTime;
}
