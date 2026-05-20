package com.neusoft.health.modules.consultation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.modules.consultation.entity.FaqCategory;
import com.neusoft.health.common.enums.UserStatusEnum;
import com.neusoft.health.modules.consultation.mapper.FaqCategoryMapper;
import com.neusoft.health.modules.consultation.service.FaqCategoryService;
import com.neusoft.health.modules.consultation.service.FaqService;
import com.neusoft.health.modules.consultation.vo.FaqCategoryTreeVO;
import com.neusoft.health.modules.consultation.vo.FaqCategoryVO;
import com.neusoft.health.modules.consultation.vo.FaqVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaqCategoryServiceImpl extends ServiceImpl<FaqCategoryMapper, FaqCategory> implements FaqCategoryService {

    private final FaqService faqService;

    @Override
    public List<FaqCategoryVO> listCategories() {
        return listActive();
    }

    @Override
    public List<FaqCategoryTreeVO> listCategoryTree() {
        return listWithFaqs();
    }

    @Override
    public List<FaqVO> listFaqsByCategory(Long categoryId) {
        return faqService.listByCategory(categoryId);
    }

    @Override
    public List<FaqCategoryVO> listActive() {
        List<FaqCategory> categories = list(new LambdaQueryWrapper<FaqCategory>()
                .eq(FaqCategory::getStatus, UserStatusEnum.NORMAL.getCode())
                .orderByAsc(FaqCategory::getSortOrder));
        return categories.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<FaqCategoryTreeVO> listWithFaqs() {
        List<FaqCategory> categories = list(new LambdaQueryWrapper<FaqCategory>()
                .eq(FaqCategory::getStatus, UserStatusEnum.NORMAL.getCode())
                .orderByAsc(FaqCategory::getSortOrder));
        return categories.stream().map(category -> {
            FaqCategoryTreeVO tree = new FaqCategoryTreeVO();
            BeanUtils.copyProperties(category, tree);
            List<FaqVO> faqs = faqService.listActiveByCategory(category.getId());
            tree.setFaqList(faqs);
            return tree;
        }).collect(Collectors.toList());
    }

    private FaqCategoryVO toVO(FaqCategory category) {
        FaqCategoryVO vo = new FaqCategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
}
