package com.neusoft.health.modules.consultation.service.impl;

import com.neusoft.health.framework.ai.AiClient;
import com.neusoft.health.modules.consultation.service.AssistantService;
import com.neusoft.health.modules.consultation.vo.AssistantResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssistantServiceImpl implements AssistantService {

    private final AiClient aiClient;

    @Override
    public AssistantResponseVO query(String feature, String healthProfile) {
        String systemPrompt = getSystemPrompt(feature);
        String userMessage = buildUserMessage(healthProfile);

        log.info("AI助手请求: feature={}, hasProfile={}", feature, healthProfile != null && !healthProfile.isEmpty());

        String aiReply = aiClient.chat(systemPrompt, userMessage);

        if (aiReply == null || aiReply.isEmpty()) {
            log.error("AI助手返回为空: feature={}", feature);
            AssistantResponseVO vo = new AssistantResponseVO();
            vo.setFeature(feature);
            vo.setContent("AI服务暂时不可用，请稍后再试");
            vo.setDisclaimer("以上内容由AI生成，仅供参考，不构成医疗诊断建议。如有身体不适，请及时就医。");
            return vo;
        }

        log.info("AI助手响应完成: feature={}, replyLength={}", feature, aiReply.length());

        AssistantResponseVO vo = new AssistantResponseVO();
        vo.setFeature(feature);
        vo.setContent(aiReply);
        vo.setDisclaimer("以上内容由AI生成，仅供参考，不构成医疗诊断建议。如有身体不适，请及时就医。");
        return vo;
    }

    private String getSystemPrompt(String feature) {
        return switch (feature) {
            case "daily_tips" -> "你是一个专业的健康管理助手。请根据当前季节和时间，为用户提供3-5条实用的每日健康小贴士。内容包括：饮食建议、运动建议、作息建议、心理健康等方面。请分点说明，每条简洁实用，控制在20字以内。";
            case "health_assessment" -> "你是一个专业的健康管理助手。请根据用户提供的健康档案信息，进行全面的健康评估分析。包括：整体健康评分、各指标分析、潜在风险提示、改善建议等。如果用户未提供健康档案，请提醒用户先完善健康档案。请分点说明，条理清晰。";
            case "lifestyle_advice" -> "你是一个专业的健康管理助手。请根据用户的健康档案信息，提供个性化的生活方式建议，包括：饮食计划建议、运动方案建议、作息调整建议、心理健康建议等。如果用户未提供健康档案，请给出通用的健康生活建议。请分点说明，切实可行。";
            default -> throw new IllegalArgumentException("不支持的功能类型: " + feature);
        };
    }

    private String buildUserMessage(String healthProfile) {
        if (healthProfile != null && !healthProfile.isEmpty()) {
            return "我的健康档案信息：" + healthProfile;
        }
        return "请提供通用建议";
    }
}