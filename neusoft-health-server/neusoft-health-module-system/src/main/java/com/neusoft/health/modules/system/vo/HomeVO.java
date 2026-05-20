package com.neusoft.health.modules.system.vo;

import com.neusoft.health.modules.consultation.vo.FaqCategoryTreeVO;
import lombok.Data;

import java.util.List;

@Data
public class HomeVO {

    private List<FaqCategoryTreeVO> faqCategories;

    private List<HealthArticleVO> articles;

    private String disclaimer;
}
