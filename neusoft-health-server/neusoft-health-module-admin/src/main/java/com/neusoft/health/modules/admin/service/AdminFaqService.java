package com.neusoft.health.modules.admin.service;

import com.neusoft.health.modules.consultation.entity.Faq;
import com.neusoft.health.modules.consultation.entity.FaqCategory;
import com.neusoft.health.modules.consultation.vo.FaqVO;

import java.util.List;

/**
 * FAQ管理服务
 * <p>
 * 提供FAQ和分类的增删改查功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface AdminFaqService {

    /**
     * 获取FAQ列表
     *
     * @param categoryId 分类ID
     * @return FAQ列表
     */
    List<FaqVO> listAllFaqs(Long categoryId);

    /**
     * 创建FAQ
     *
     * @param faq FAQ实体
     * @return 创建后的FAQ
     */
    Faq createFaq(Faq faq);

    /**
     * 更新FAQ
     *
     * @param faq FAQ实体
     * @return 更新后的FAQ
     */
    Faq updateFaq(Faq faq);

    /**
     * 删除FAQ
     *
     * @param id FAQ ID
     */
    void deleteFaq(Long id);

    /**
     * 创建FAQ分类
     *
     * @param category 分类实体
     * @return 创建后的分类
     */
    FaqCategory createCategory(FaqCategory category);

    /**
     * 更新FAQ分类
     *
     * @param category 分类实体
     * @return 更新后的分类
     */
    FaqCategory updateCategory(FaqCategory category);

    /**
     * 删除FAQ分类
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);

    /**
     * 获取所有FAQ分类
     *
     * @return 分类列表
     */
    List<FaqCategory> listAllCategories();
}
