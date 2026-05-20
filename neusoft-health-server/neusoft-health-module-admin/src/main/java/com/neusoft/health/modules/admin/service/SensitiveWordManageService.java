package com.neusoft.health.modules.admin.service;

import com.neusoft.health.modules.consultation.entity.SensitiveWord;
import com.neusoft.health.modules.consultation.vo.SensitiveWordVO;

import java.util.List;

/**
 * 敏感词管理服务
 * <p>
 * 提供敏感词的增删改查功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface SensitiveWordManageService {

    /**
     * 获取所有敏感词列表
     *
     * @return 敏感词列表
     */
    List<SensitiveWordVO> listAll();

    /**
     * 创建敏感词
     *
     * @param word 敏感词实体
     * @return 创建后的敏感词
     */
    SensitiveWord create(SensitiveWord word);

    /**
     * 更新敏感词
     *
     * @param word 敏感词实体
     * @return 更新后的敏感词
     */
    SensitiveWord updateWord(SensitiveWord word);

    /**
     * 删除敏感词
     *
     * @param id 敏感词ID
     */
    void deleteWord(Long id);
}
