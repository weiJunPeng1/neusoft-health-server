package com.neusoft.health.modules.consultation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.consultation.dto.MessageReviewDTO;
import com.neusoft.health.modules.consultation.dto.MessageSendDTO;
import com.neusoft.health.modules.consultation.entity.Message;
import com.neusoft.health.modules.consultation.vo.EmergencyLogVO;
import com.neusoft.health.modules.consultation.vo.MessageDetailVO;
import com.neusoft.health.modules.consultation.vo.MessageVO;

import java.util.List;

/**
 * 咨询消息服务
 * <p>
 * 提供消息发送、查询、审核等功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface MessageService extends IService<Message> {

    /**
     * 获取消息列表
     *
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<MessageVO> listMessages(Long userId, Long sessionId);

    /**
     * 获取紧急日志列表
     *
     * @param userId 用户ID
     * @return 紧急日志列表
     */
    List<EmergencyLogVO> listEmergencyLogs(Long userId);

    /**
     * 处理紧急情况
     *
     * @param id 日志ID
     */
    void handleEmergency(Long id);

    /**
     * 发送消息
     *
     * @param userId 用户ID
     * @param dto 消息发送DTO
     * @return 消息VO
     */
    MessageVO sendMessage(Long userId, MessageSendDTO dto);

    /**
     * 获取会话消息列表
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<MessageVO> listSessionMessages(Long sessionId);

    /**
     * 获取消息详情
     *
     * @param id 消息ID
     * @return 消息详情VO
     */
    MessageDetailVO getMessageDetail(Long id);

    /**
     * 审核消息
     *
     * @param dto 审核DTO
     * @param reviewerId 审核人ID
     */
    void reviewMessage(MessageReviewDTO dto, Long reviewerId);

    /**
     * 获取待审核消息列表
     *
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表
     */
    List<MessageDetailVO> listPendingReview(int page, int size);
}

