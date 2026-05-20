package com.neusoft.health.modules.consultation.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SensitiveWordVO {

    private Long id;

    private String word;

    private String category;

    private Integer status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
