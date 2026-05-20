package com.neusoft.health.modules.consultation.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.health.common.enums.UserStatusEnum;
import com.neusoft.health.modules.consultation.entity.Faq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FaqMapper extends BaseMapper<Faq> {

    default List<Faq> findActiveByCategory(Long categoryId) {
        return selectList(new LambdaQueryWrapper<Faq>()
                .eq(Faq::getCategoryId, categoryId)
                .eq(Faq::getStatus, UserStatusEnum.NORMAL.getCode())
                .orderByAsc(Faq::getSortOrder));
    }
}

