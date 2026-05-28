package com.neusoft.health.modules.consultation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.consultation.dto.SessionCreateDTO;
import com.neusoft.health.modules.consultation.entity.Session;
import com.neusoft.health.modules.consultation.vo.SessionVO;

import java.util.List;

/**
 * 咨询会话服务
 * <p>
 * 提供咨询会话的创建、查询、删除等功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface SessionService extends IService<Session> {

    /**
     * 创建会话
     *
     * @param userId 用户ID
     * @param dto 会话创建DTO
     * @return 会话VO
     */
    SessionVO createSession(Long userId, SessionCreateDTO dto);

    /**
     * 获取会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<SessionVO> listSessions(Long userId);

    /**
     * 删除会话
     *
     * @param userId 用户ID
     * @param sessionId 会话ID
     */
    void deleteSession(Long userId, Long sessionId);

    /**
     * 获取用户会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<SessionVO> listUserSessions(Long userId);

    /**
     * 创建会话
     *
     * @param userId 用户ID
     * @param firstMessage 第一条消息
     * @return 会话实体
     */
    Session createSession(Long userId, String firstMessage);
}

