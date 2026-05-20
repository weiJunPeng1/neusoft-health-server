package com.neusoft.health.modules.consultation.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SessionVO {

    private Long id;

    private Long userId;

    private String title;

    private Integer messageCount;

    private LocalDateTime lastMessageAt;

    private Integer status;

    private LocalDateTime createdTime;
}
