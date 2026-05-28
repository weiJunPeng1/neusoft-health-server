package com.neusoft.health.modules.consultation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.modules.consultation.dto.SessionCreateDTO;
import com.neusoft.health.modules.consultation.entity.Session;
import com.neusoft.health.modules.consultation.mapper.SessionMapper;
import com.neusoft.health.modules.consultation.service.SessionService;
import com.neusoft.health.modules.consultation.vo.SessionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session> implements SessionService {

    @Override
    public SessionVO createSession(Long userId, SessionCreateDTO dto) {
        Session session = new Session();
        session.setUserId(userId);
        String title = dto.getFirstMessage();
        session.setTitle(title.length() > 20 ? title.substring(0, 20) : title);
        session.setMessageCount(0);
        session.setLastMessageAt(LocalDateTime.now());
        session.setStatus(0);
        save(session);
        return toVO(session);
    }

    @Override
    public List<SessionVO> listSessions(Long userId) {
        return listUserSessions(userId);
    }

    @Override
    public void deleteSession(Long userId, Long sessionId) {
        Session session = getById(sessionId);
        if (session != null && session.getUserId().equals(userId)) {
            removeById(sessionId);
        }
    }

    @Override
    public List<SessionVO> listUserSessions(Long userId) {
        List<Session> sessions = list(new LambdaQueryWrapper<Session>()
                .eq(Session::getUserId, userId)
                .orderByDesc(Session::getLastMessageAt));
        return sessions.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public Session createSession(Long userId, String firstMessage) {
        Session session = new Session();
        session.setUserId(userId);
        session.setTitle(firstMessage.length() > 20 ? firstMessage.substring(0, 20) : firstMessage);
        session.setMessageCount(0);
        session.setLastMessageAt(LocalDateTime.now());
        session.setStatus(0);
        save(session);
        return session;
    }

    private SessionVO toVO(Session session) {
        SessionVO vo = new SessionVO();
        BeanUtils.copyProperties(session, vo);
        return vo;
    }
}