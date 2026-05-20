package com.neusoft.health.modules.consultation.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.health.common.enums.UserStatusEnum;
import com.neusoft.health.modules.consultation.entity.FaqCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FaqCategoryMapper extends BaseMapper<FaqCategory> {

    default List<FaqCategory> findActiveCategories() {
        return selectList(new LambdaQueryWrapper<FaqCategory>()
                .eq(FaqCategory::getStatus, UserStatusEnum.NORMAL.getCode())
                .orderByAsc(FaqCategory::getSortOrder));
    }
}

