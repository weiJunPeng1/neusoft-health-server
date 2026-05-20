package com.neusoft.health.modules.admin.service;

import com.neusoft.health.modules.consultation.dto.MessageReviewDTO;
import com.neusoft.health.modules.consultation.vo.MessageDetailVO;

import java.util.List;

/**
 * 内容审核服务
 * <p>
 * 提供待审核消息查询和审核功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface ContentReviewService {

    /**
     * 获取待审核消息列表
     *
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表
     */
    List<MessageDetailVO> listPendingReview(int page, int size);

    /**
     * 审核消息
     *
     * @param dto 审核DTO
     * @param reviewerId 审核人ID
     */
    void reviewMessage(MessageReviewDTO dto, Long reviewerId);
}
