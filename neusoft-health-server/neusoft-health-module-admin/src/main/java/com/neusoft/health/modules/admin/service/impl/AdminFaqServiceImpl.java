package com.neusoft.health.modules.admin.service.impl;

import com.neusoft.health.modules.admin.service.AdminFaqService;
import com.neusoft.health.modules.consultation.entity.Faq;
import com.neusoft.health.modules.consultation.entity.FaqCategory;
import com.neusoft.health.modules.consultation.mapper.FaqCategoryMapper;
import com.neusoft.health.modules.consultation.mapper.FaqMapper;
import com.neusoft.health.modules.consultation.vo.FaqVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminFaqServiceImpl implements AdminFaqService {

    private final FaqMapper faqMapper;
    private final FaqCategoryMapper faqCategoryMapper;

    @Override
    public List<FaqVO> listAllFaqs(Long categoryId) {
        List<Faq> faqs;
        if (categoryId != null) {
            faqs = faqMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Faq>()
                            .eq(Faq::getCategoryId, categoryId)
                            .orderByAsc(Faq::getSortOrder));
        } else {
            faqs = faqMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Faq>()
                            .orderByAsc(Faq::getSortOrder));
        }
        List<FaqCategory> categories = faqCategoryMapper.selectList(null);
        Map<Long, String> categoryNameMap = categories.stream()
                .collect(Collectors.toMap(FaqCategory::getId, FaqCategory::getName));
        return faqs.stream().map(faq -> {
            FaqVO vo = new FaqVO();
            BeanUtils.copyProperties(faq, vo);
            vo.setCategoryName(categoryNameMap.get(faq.getCategoryId()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public Faq createFaq(Faq faq) {
        faqMapper.insert(faq);
        return faq;
    }

    @Override
    public Faq updateFaq(Faq faq) {
        faqMapper.updateById(faq);
        return faq;
    }

    @Override
    public void deleteFaq(Long id) {
        faqMapper.deleteById(id);
    }

    @Override
    public FaqCategory createCategory(FaqCategory category) {
        faqCategoryMapper.insert(category);
        return category;
    }

    @Override
    public FaqCategory updateCategory(FaqCategory category) {
        faqCategoryMapper.updateById(category);
        return category;
    }

    @Override
    public void deleteCategory(Long id) {
        faqCategoryMapper.deleteById(id);
    }

    @Override
    public List<FaqCategory> listAllCategories() {
        return faqCategoryMapper.selectList(null);
    }
}
