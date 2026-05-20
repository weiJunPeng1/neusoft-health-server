package com.neusoft.health.modules.consultation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.consultation.entity.SensitiveWord;
import com.neusoft.health.modules.consultation.vo.SensitiveWordCheckVO;
import com.neusoft.health.modules.consultation.vo.SensitiveWordVO;

import java.util.List;

/**
 * 敏感词服务
 * <p>
 * 提供敏感词的查询、检查、过滤等功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface SensitiveWordService extends IService<SensitiveWord> {

    /**
     * 获取敏感词列表
     *
     * @return 敏感词列表
     */
    List<SensitiveWordVO> listWords();

    /**
     * 检查内容中的敏感词
     *
     * @param content 内容
     * @return 检查结果VO
     */
    SensitiveWordCheckVO check(String content);

    /**
     * 获取活跃敏感词列表
     *
     * @return 敏感词列表
     */
    List<SensitiveWord> listActiveWords();

    /**
     * 过滤敏感词
     *
     * @param content 内容
     * @return 过滤后的内容
     */
    String filterSensitiveWords(String content);

    /**
     * 检查内容是否包含敏感词
     *
     * @param content 内容
     * @return 是否包含
     */
    boolean containsSensitiveWord(String content);

    /**
     * 获取紧急关键词
     *
     * @param content 内容
     * @return 紧急关键词
     */
    String getEmergencyKeyword(String content);

    /**
     * 检查内容是否为紧急内容
     *
     * @param content 内容
     * @return 是否为紧急内容
     */
    boolean isEmergencyContent(String content);
}

