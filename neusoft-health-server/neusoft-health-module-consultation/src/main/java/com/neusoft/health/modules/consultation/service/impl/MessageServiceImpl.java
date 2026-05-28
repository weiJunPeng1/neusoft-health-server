package com.neusoft.health.modules.consultation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.framework.ai.AiClient;
import com.neusoft.health.framework.ai.EmbeddingClassifier;
import com.neusoft.health.modules.consultation.dto.MessageReviewDTO;
import com.neusoft.health.common.enums.ConsultCategoryEnum;
import com.neusoft.health.common.enums.MessageRoleEnum;
import com.neusoft.health.common.enums.ReviewStatusEnum;
import com.neusoft.health.modules.consultation.dto.MessageSendDTO;
import com.neusoft.health.modules.consultation.entity.Message;
import com.neusoft.health.modules.consultation.entity.Session;
import com.neusoft.health.modules.consultation.mapper.MessageMapper;
import com.neusoft.health.common.exception.BusinessException;
import com.neusoft.health.common.result.ResultCode;
import com.neusoft.health.modules.consultation.service.EmergencyService;
import com.neusoft.health.modules.consultation.service.MessageService;
import com.neusoft.health.modules.consultation.service.SensitiveWordService;
import com.neusoft.health.modules.consultation.service.SessionService;
import com.neusoft.health.modules.member.service.MemberService;
import com.neusoft.health.modules.member.vo.MemberStatusVO;
import com.neusoft.health.modules.consultation.vo.EmergencyLogVO;
import com.neusoft.health.modules.consultation.vo.MessageDetailVO;
import com.neusoft.health.modules.consultation.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.Objects;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final SessionService sessionService;
    private final SensitiveWordService sensitiveWordService;
    private final EmergencyService emergencyService;
    private final AiClient aiClient;
    private final EmbeddingClassifier embeddingClassifier;
    private final MemberService memberService;
    private final com.neusoft.health.common.mapper.UserMapper userMapper;

    @Override
    public List<MessageVO> listMessages(Long userId, Long sessionId) {
        List<Message> messages = list(new LambdaQueryWrapper<Message>()
                .eq(Message::getSessionId, sessionId)
                .orderByAsc(Message::getCreatedTime));
        return messages.stream().map(this::toMessageVO).collect(Collectors.toList());
    }

    @Override
    public List<EmergencyLogVO> listEmergencyLogs(Long userId) {
        return emergencyService.listEmergencyLogs(userId);
    }

    @Override
    public void handleEmergency(Long id) {
        emergencyService.handleEmergency(id);
    }

    @Override
    public MessageVO sendMessage(Long userId, MessageSendDTO dto) {
        // 参数验证
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (dto == null || dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }

        String content = dto.getContent().trim();
        
        // 内容长度限制
        if (content.length() > 1000) {
            log.warn("Message content too long: userId={}, length={}", userId, content.length());
            throw new IllegalArgumentException("消息内容过长，请简化后发送");
        }

        // 检查咨询配额
        checkConsultQuota(userId);
        
        Session session;
        boolean isNewSession = false;
        if (dto.getSessionId() == null) {
            session = sessionService.createSession(userId, content);
            isNewSession = true;
        } else {
            session = sessionService.getById(dto.getSessionId());
            if (session == null) {
                log.warn("Session not found: sessionId={}", dto.getSessionId());
                session = sessionService.createSession(userId, content);
                isNewSession = true;
            }
        }

        log.info("Sending message: userId={}, sessionId={}, isNewSession={}, contentLength={}", 
                userId, session.getId(), isNewSession, content.length());

        long startTime = System.currentTimeMillis();

        Message userMessage = new Message();
        userMessage.setSessionId(session.getId());
        userMessage.setUserId(userId);
        userMessage.setRole(MessageRoleEnum.USER.getCode());
        userMessage.setContent(content);
        userMessage.setContentType(1);

        boolean hasSensitive = sensitiveWordService.containsSensitiveWord(content);
        if (hasSensitive) {
            userMessage.setIsViolation(1);
            userMessage.setViolationReason("内容包含敏感词，等待人工审核");
            userMessage.setReviewStatus(ReviewStatusEnum.PENDING.getCode());
            log.warn("Message contains sensitive words, intercepted: userId={}", userId);
        } else {
            userMessage.setReviewStatus(ReviewStatusEnum.APPROVED.getCode());
        }

        boolean isEmergency = sensitiveWordService.isEmergencyContent(content);
        String emergencyKeyword = null;
        if (isEmergency) {
            emergencyKeyword = sensitiveWordService.getEmergencyKeyword(content);
            userMessage.setIsEmergency(1);
            userMessage.setEmergencyKeyword(emergencyKeyword);
            log.info("Emergency content detected: userId={}, keyword={}", userId, emergencyKeyword);
        }

        save(userMessage);
        log.debug("User message saved: messageId={}", userMessage.getId());

        if (userMessage.getIsEmergency() != null && userMessage.getIsEmergency() == 1) {
            emergencyService.createEmergencyLog(userId, userMessage.getId(), emergencyKeyword);
        }

        if (hasSensitive) {
            session.setMessageCount(session.getMessageCount() + 1);
            session.setLastMessageAt(LocalDateTime.now());
            sessionService.updateById(session);

            MessageVO waitVo = new MessageVO();
            waitVo.setIsEmergency(0);
            waitVo.setDisclaimer("您的消息包含敏感内容，正在等待人工审核，审核通过后将为您生成回复，请稍后查看。");
            return waitVo;
        }

        String systemPrompt = "你是一个专业的健康咨询助手，由东软健康提供。请基于医学常识，为用户提供专业、准确、易懂的健康咨询建议。" +
                "请注意：你的回答仅供参考，不能替代专业医疗诊断。如有严重症状，请及时就医。" +
                "回答应当简洁明了，控制在200字以内。";

        // 获取历史消息（排除刚刚保存的消息，避免重复）
        List<Message> historyList = list(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getSessionId, session.getId())
                        .ne(Message::getId, userMessage.getId())
                        .orderByAsc(Message::getCreatedTime)
        );

        log.debug("History messages count: {}", historyList.size());

        List<Map<String, String>> history = historyList.stream()
                .map(msg -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("role", msg.getRole());
                    map.put("content", msg.getContent());
                    return map;
                })
                .collect(Collectors.toList());

        // 调用AI
        String aiReply = aiClient.chat(systemPrompt, content, history);
        long duration = System.currentTimeMillis() - startTime;

        // AI调用失败处理
        if (aiReply == null || aiReply.isEmpty()) {
            log.error("AI returned empty response: userId={}, sessionId={}", userId, session.getId());
            MessageVO vo = new MessageVO();
            vo.setDisclaimer("AI服务暂时不可用，请稍后再试");
            return vo;
        }

        log.info("AI response received: userId={}, sessionId={}, duration={}ms, replyLength={}", 
                userId, session.getId(), duration, aiReply.length());

        Message aiMessage = new Message();
        aiMessage.setSessionId(session.getId());
        aiMessage.setUserId(userId);
        aiMessage.setRole(MessageRoleEnum.ASSISTANT.getCode());
        aiMessage.setContent(aiReply);
        aiMessage.setContentType(1);
        aiMessage.setApiCallDuration((int) duration);
        aiMessage.setReviewStatus(ReviewStatusEnum.APPROVED.getCode());
        save(aiMessage);
        log.debug("AI message saved: messageId={}", aiMessage.getId());

        session.setMessageCount(session.getMessageCount() + 2);
        session.setLastMessageAt(LocalDateTime.now());

        if (isNewSession && (session.getCategory() == null || session.getCategory().isBlank())) {
            classifySessionAsync(session, content);
        }

        sessionService.updateById(session);

        MessageVO vo = toMessageVO(aiMessage);
        if (userMessage.getIsEmergency() != null && userMessage.getIsEmergency() == 1) {
            vo.setIsEmergency(1);
        }
        vo.setDisclaimer("以上内容由AI生成，仅供参考，不构成医疗诊断建议。如有身体不适，请及时就医。");
        return vo;
    }

    @Override
    public List<MessageVO> listSessionMessages(Long sessionId) {
        List<Message> messages = list(new LambdaQueryWrapper<Message>()
                .eq(Message::getSessionId, sessionId)
                .orderByAsc(Message::getCreatedTime));
        return messages.stream().map(this::toMessageVO).collect(Collectors.toList());
    }

    @Override
    public MessageDetailVO getMessageDetail(Long id) {
        Message message = getById(id);
        if (message == null) {
            return null;
        }
        MessageDetailVO vo = new MessageDetailVO();
        BeanUtils.copyProperties(message, vo);
        return vo;
    }

    @Override
    public void reviewMessage(MessageReviewDTO dto, Long reviewerId) {
        Message message = getById(dto.getMessageId());
        if (message == null) {
            return;
        }

        boolean isApproved = Objects.equals(ReviewStatusEnum.APPROVED.getCode(), dto.getReviewStatus());

        message.setReviewStatus(dto.getReviewStatus());
        message.setReviewedBy(reviewerId);
        message.setReviewedAt(LocalDateTime.now());
        if (dto.getViolationReason() != null) {
            message.setViolationReason(dto.getViolationReason());
        }
        if (dto.getModifiedContent() != null) {
            message.setModifiedContent(dto.getModifiedContent());
        }
        if (Objects.equals(ReviewStatusEnum.VIOLATION.getCode(), dto.getReviewStatus())) {
            message.setIsViolation(1);
        }
        updateById(message);

        if (isApproved && message.getRole().equals(MessageRoleEnum.USER.getCode())) {
            generateReplyAfterApproval(message);
        }
    }

    private void generateReplyAfterApproval(Message userMessage) {
        try {
            String systemPrompt = "你是一个专业的健康咨询助手，由东软健康提供。请基于医学常识，为用户提供专业、准确、易懂的健康咨询建议。" +
                    "请注意：你的回答仅供参考，不能替代专业医疗诊断。如有严重症状，请及时就医。" +
                    "回答应当简洁明了，控制在200字以内。";

            List<Message> historyList = list(
                    new LambdaQueryWrapper<Message>()
                            .eq(Message::getSessionId, userMessage.getSessionId())
                            .ne(Message::getId, userMessage.getId())
                            .orderByAsc(Message::getCreatedTime)
            );

            List<Map<String, String>> history = historyList.stream()
                    .map(msg -> {
                        Map<String, String> map = new HashMap<>();
                        map.put("role", msg.getRole());
                        map.put("content", msg.getContent());
                        return map;
                    })
                    .collect(Collectors.toList());

            long startTime = System.currentTimeMillis();
            String aiReply = aiClient.chat(systemPrompt, userMessage.getContent(), history);
            long duration = System.currentTimeMillis() - startTime;

            if (aiReply == null || aiReply.isEmpty()) {
                log.error("AI reply generation failed after approval: messageId={}", userMessage.getId());
                return;
            }

            Message aiMessage = new Message();
            aiMessage.setSessionId(userMessage.getSessionId());
            aiMessage.setUserId(userMessage.getUserId());
            aiMessage.setRole(MessageRoleEnum.ASSISTANT.getCode());
            aiMessage.setContent(aiReply);
            aiMessage.setContentType(1);
            aiMessage.setApiCallDuration((int) duration);
            aiMessage.setReviewStatus(ReviewStatusEnum.APPROVED.getCode());
            save(aiMessage);

            Session session = sessionService.getById(userMessage.getSessionId());
            if (session != null) {
                session.setMessageCount(session.getMessageCount() + 1);
                session.setLastMessageAt(LocalDateTime.now());
                sessionService.updateById(session);
            }

            log.info("AI reply generated after approval: messageId={}, duration={}ms", userMessage.getId(), duration);
        } catch (Exception e) {
            log.error("Failed to generate AI reply after approval: messageId={}", userMessage.getId(), e);
        }
    }

    @Override
    public List<MessageDetailVO> listPendingReview(int page, int size) {
        List<Message> messages = list(new LambdaQueryWrapper<Message>()
                .eq(Message::getReviewStatus, ReviewStatusEnum.PENDING.getCode())
                .orderByAsc(Message::getCreatedTime));
        List<Long> userIds = messages.stream().map(Message::getUserId).distinct().collect(Collectors.toList());
        Map<Long, com.neusoft.health.common.entity.User> userMap = userIds.isEmpty() ? Map.of() :
                userMapper.selectBatchIds(userIds).stream()
                        .collect(Collectors.toMap(com.neusoft.health.common.entity.User::getId, u -> u));
        return messages.stream().map(msg -> {
            MessageDetailVO vo = new MessageDetailVO();
            BeanUtils.copyProperties(msg, vo);
            com.neusoft.health.common.entity.User user = userMap.get(msg.getUserId());
            if (user != null) {
                vo.setNickname(user.getNickname());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    private MessageVO toMessageVO(Message message) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(message, vo);
        return vo;
    }
    
    private void checkConsultQuota(Long userId) {
        MemberStatusVO status = memberService.getMemberStatus(userId);
        String levelCode = status.getLevelCode();

        if (levelCode == null || "L0".equals(levelCode)) {
            Integer dailyQuota = status.getDailyQuota() != null ? status.getDailyQuota() : 3;
            Integer todayUsed = status.getTodayUsed() != null ? status.getTodayUsed() : 0;

            if (todayUsed >= dailyQuota) {
                throw new BusinessException(50001, "今日免费咨询额度已用完，请开通会员享受更多咨询次数");
            }
        }
    }

    private void classifySessionAsync(Session session, String userContent) {
        try {
            ConsultCategoryEnum category = embeddingClassifier.classify(userContent);
            session.setCategory(category.getLabel());
            log.info("Session classified by embedding: sessionId={}, category={}", session.getId(), category.getLabel());
        } catch (Exception e) {
            log.warn("Session classification failed, sessionId={}: {}", session.getId(), e.getMessage());
        }
    }
}