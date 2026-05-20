package com.neusoft.health.framework.ai;

import com.neusoft.health.framework.config.DeepSeekProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * DeepSeek AI客户端
 * <p>
 * 用于调用DeepSeek的聊天API，实现智能对话功能。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeepSeekClient {

    /**
     * REST模板
     */
    private final RestTemplate restTemplate;
    /**
     * DeepSeek配置属性
     */
    private final DeepSeekProperties deepSeekProperties;

    /**
     * 发送聊天请求（无历史消息）
     *
     * @param systemPrompt 系统提示词
     * @param userMessage  用户消息
     * @return AI响应
     */
    public String chat(String systemPrompt, String userMessage) {
        return chat(systemPrompt, userMessage, null);
    }

    /**
     * 发送聊天请求（含历史消息）
     *
     * @param systemPrompt 系统提示词
     * @param userMessage  用户消息
     * @param history      历史消息列表
     * @return AI响应
     */
    public String chat(String systemPrompt, String userMessage, List<Map<String, String>> history) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", deepSeekProperties.getModel());
            requestBody.put("max_tokens", deepSeekProperties.getMaxTokens());
            requestBody.put("temperature", deepSeekProperties.getTemperature());

            List<Map<String, String>> messages = new ArrayList<>();

            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                Map<String, String> systemMsg = new HashMap<>();
                systemMsg.put("role", "system");
                systemMsg.put("content", systemPrompt);
                messages.add(systemMsg);
            }

            if (history != null) {
                messages.addAll(history);
            }

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            requestBody.put("messages", messages);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setBearerAuth(deepSeekProperties.getApiKey());
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            org.springframework.http.HttpEntity<Map<String, Object>> request =
                    new org.springframework.http.HttpEntity<>(requestBody, headers);

            var response = restTemplate.postForEntity(
                    deepSeekProperties.getApiUrl() + "/v1/chat/completions",
                    request,
                    Map.class
            );

            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    if (message != null) {
                        return message.get("content");
                    }
                }
            }
            return null;
        } catch (Exception e) {
            log.error("DeepSeek API call failed", e);
            return "抱歉，AI 服务暂时不可用，请稍后再试。";
        }
    }
}
