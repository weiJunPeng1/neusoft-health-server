package com.neusoft.health.modules.consultation.vo;

import lombok.Data;
import java.util.List;

@Data
public class FaqCategoryTreeVO {

    private Long id;

    private String name;

    private String icon;

    private Integer sortOrder;

    private List<FaqVO> faqList;
}
