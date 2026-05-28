package com.neusoft.health.modules.consultation.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FaqVO {

    private Long id;

    private Long categoryId;

    private String categoryName;

    private String question;

    private String presetAnswer;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createdTime;
}
