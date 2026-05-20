package com.neusoft.health.modules.consultation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.framework.ai.DeepSeekClient;
import com.neusoft.health.modules.consultation.dto.MessageReviewDTO;
import com.neusoft.health.common.enums.MessageRoleEnum;
import com.neusoft.health.common.enums.ReviewStatusEnum;
import com.neusoft.health.modules.consultation.dto.MessageSendDTO;
import com.neusoft.health.modules.consultation.entity.Message;
import com.neusoft.health.modules.consultation.entity.Session;
import com.neusoft.health.modules.consultation.mapper.MessageMapper;
import com.neusoft.health.modules.consultation.service.EmergencyService;
import com.neusoft.health.modules.consultation.service.MessageService;
import com.neusoft.health.modules.consultation.service.SensitiveWordService;
import com.neusoft.health.modules.consultation.service.SessionService;
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
    private final DeepSeekClient deepSeekClient;

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
        Session session;
        boolean isNewSession = false;
        if (dto.getSessionId() == null) {
            session = sessionService.createSession(userId, dto.getContent());
            isNewSession = true;
        } else {
            session = sessionService.getById(dto.getSessionId());
        }

        long startTime = System.currentTimeMillis();

        String filteredContent = sensitiveWordService.filterSensitiveWords(dto.getContent());

        Message userMessage = new Message();
        userMessage.setSessionId(session.getId());
        userMessage.setUserId(userId);
        userMessage.setRole(MessageRoleEnum.USER.getCode());
        userMessage.setContent(dto.getContent());
        userMessage.setContentType(1);

        boolean hasSensitive = sensitiveWordService.containsSensitiveWord(dto.getContent());
        if (hasSensitive) {
            userMessage.setIsViolation(1);
            userMessage.setViolationReason("内容包含敏感词");
        }

        String emergencyKeyword = sensitiveWordService.getEmergencyKeyword(dto.getContent());
        if (emergencyKeyword != null) {
            userMessage.setIsEmergency(1);
            userMessage.setEmergencyKeyword(emergencyKeyword);
        }

        userMessage.setReviewStatus(ReviewStatusEnum.PENDING.getCode());
        save(userMessage);

        if (userMessage.getIsEmergency() != null && userMessage.getIsEmergency() == 1) {
            emergencyService.createEmergencyLog(userId, userMessage.getId(), emergencyKeyword);
        }

        String systemPrompt = "你是一个专业的健康咨询助手，由东软健康提供。请基于医学常识，为用户提供专业、准确、易懂的健康咨询建议。" +
                "请注意：你的回答仅供参考，不能替代专业医疗诊断。如有严重症状，请及时就医。" +
                "回答应当简洁明了，控制在200字以内。";

        List<Message> historyList = list(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getSessionId, session.getId())
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

        String aiReply = deepSeekClient.chat(systemPrompt, dto.getContent(), history);
        long duration = System.currentTimeMillis() - startTime;

        Message aiMessage = new Message();
        aiMessage.setSessionId(session.getId());
        aiMessage.setUserId(userId);
        aiMessage.setRole(MessageRoleEnum.ASSISTANT.getCode());
        aiMessage.setContent(aiReply);
        aiMessage.setContentType(1);
        aiMessage.setApiCallDuration((int) duration);
        if (hasSensitive || emergencyKeyword != null) {
            aiMessage.setReviewStatus(ReviewStatusEnum.PENDING.getCode());
        } else {
            aiMessage.setReviewStatus(ReviewStatusEnum.APPROVED.getCode());
        }
        save(aiMessage);

        session.setMessageCount(session.getMessageCount() + 2);
        session.setLastMessageAt(LocalDateTime.now());
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
        if (message != null) {
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
        }
    }

    @Override
    public List<MessageDetailVO> listPendingReview(int page, int size) {
        List<Message> messages = list(new LambdaQueryWrapper<Message>()
                .eq(Message::getReviewStatus, ReviewStatusEnum.PENDING.getCode())
                .orderByAsc(Message::getCreatedTime));
        return messages.stream().map(msg -> {
            MessageDetailVO vo = new MessageDetailVO();
            BeanUtils.copyProperties(msg, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    private MessageVO toMessageVO(Message message) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(message, vo);
        return vo;
    }

    private MessageVO toVO(Message message) {
        return toMessageVO(message);
    }
}