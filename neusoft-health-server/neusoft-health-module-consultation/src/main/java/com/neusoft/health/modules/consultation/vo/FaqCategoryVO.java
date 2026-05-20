package com.neusoft.health.modules.consultation.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FaqCategoryVO {

    private Long id;

    private String name;

    private String icon;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createdTime;
}
