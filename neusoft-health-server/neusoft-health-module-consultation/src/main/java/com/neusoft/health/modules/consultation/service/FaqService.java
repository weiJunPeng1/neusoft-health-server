package com.neusoft.health.modules.consultation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.consultation.entity.Faq;
import com.neusoft.health.modules.consultation.vo.FaqVO;

import java.util.List;

/**
 * FAQ服务
 * <p>
 * 提供FAQ的查询功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface FaqService extends IService<Faq> {

    /**
     * 根据分类获取FAQ列表
     *
     * @param categoryId 分类ID
     * @return FAQ列表
     */
    List<FaqVO> listByCategory(Long categoryId);

    /**
     * 根据分类获取活跃FAQ列表
     *
     * @param categoryId 分类ID
     * @return FAQ列表
     */
    List<FaqVO> listActiveByCategory(Long categoryId);
}
