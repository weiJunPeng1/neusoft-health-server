package com.neusoft.health.modules.consultation.vo;

import lombok.Data;

@Data
public class HealthSearchVO {

    private Long id;

    private String keyword;

    private String content;

    private Integer source;

    private String disclaimer;
}
