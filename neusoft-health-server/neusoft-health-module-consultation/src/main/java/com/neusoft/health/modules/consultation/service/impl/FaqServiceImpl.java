package com.neusoft.health.modules.consultation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.modules.consultation.entity.Faq;
import com.neusoft.health.common.enums.UserStatusEnum;
import com.neusoft.health.modules.consultation.mapper.FaqMapper;
import com.neusoft.health.modules.consultation.service.FaqService;
import com.neusoft.health.modules.consultation.vo.FaqVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaqServiceImpl extends ServiceImpl<FaqMapper, Faq> implements FaqService {

    @Override
    public List<FaqVO> listByCategory(Long categoryId) {
        List<Faq> faqs = list(new LambdaQueryWrapper<Faq>()
                .eq(Faq::getCategoryId, categoryId)
                .orderByAsc(Faq::getSortOrder));
        return faqs.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<FaqVO> listActiveByCategory(Long categoryId) {
        List<Faq> faqs = list(new LambdaQueryWrapper<Faq>()
                .eq(Faq::getCategoryId, categoryId)
                .eq(Faq::getStatus, UserStatusEnum.NORMAL.getCode())
                .orderByDesc(Faq::getSortOrder));
        return faqs.stream().map(this::toVO).collect(Collectors.toList());
    }

    private FaqVO toVO(Faq faq) {
        FaqVO vo = new FaqVO();
        BeanUtils.copyProperties(faq, vo);
        return vo;
    }
}

