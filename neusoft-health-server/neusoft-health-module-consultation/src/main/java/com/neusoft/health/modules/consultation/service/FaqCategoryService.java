package com.neusoft.health.modules.consultation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.consultation.entity.FaqCategory;
import com.neusoft.health.modules.consultation.vo.FaqCategoryTreeVO;
import com.neusoft.health.modules.consultation.vo.FaqCategoryVO;
import com.neusoft.health.modules.consultation.vo.FaqVO;
import com.neusoft.health.modules.consultation.vo.FaqCategoryVO;

import java.util.List;

public interface FaqCategoryService extends IService<FaqCategory> {

    List<FaqCategoryVO> listCategories();

    List<FaqCategoryTreeVO> listCategoryTree();

    List<FaqVO> listFaqsByCategory(Long categoryId);

    List<FaqCategoryVO> listActive();

    List<FaqCategoryTreeVO> listWithFaqs();
}

