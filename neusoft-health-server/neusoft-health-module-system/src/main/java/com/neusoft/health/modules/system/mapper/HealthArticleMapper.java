package com.neusoft.health.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.health.common.enums.UserStatusEnum;
import com.neusoft.health.modules.system.entity.HealthArticle;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 健康资讯Mapper
 * <p>
 * 提供健康资讯数据访问功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Mapper
public interface HealthArticleMapper extends BaseMapper<HealthArticle> {

    /**
     * 查找活跃的健康资讯
     *
     * @param limit 限制数量
     * @return 健康资讯列表
     */
    default List<HealthArticle> findActiveArticles(int limit) {
        return selectList(new LambdaQueryWrapper<HealthArticle>()
                .eq(HealthArticle::getStatus, UserStatusEnum.NORMAL.getCode())
                .orderByAsc(HealthArticle::getSortOrder)
                .last("LIMIT " + limit));
    }
}

