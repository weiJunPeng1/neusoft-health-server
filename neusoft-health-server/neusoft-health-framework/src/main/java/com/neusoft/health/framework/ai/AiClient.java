package com.neusoft.health.framework.ai;

import com.neusoft.health.common.enums.AiProvider;
import com.neusoft.health.framework.config.AiProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiClient {

    private final RestTemplate restTemplate;
    private final AiProperties aiProperties;
    private final StringRedisTemplate stringRedisTemplate;

    private final AtomicLong todayCallCount = new AtomicLong(0);
    private volatile String lastResetDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

    private static final String CALL_COUNT_KEY_PREFIX = "ai:call_count:";
    private static final String RATE_LIMIT_KEY_PREFIX = "ai:rate_limit:";

    @PostConstruct
    public void init() {
        try {
            AiProvider provider = aiProperties.getAiProvider();
            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String countStr = stringRedisTemplate.opsForValue().get(CALL_COUNT_KEY_PREFIX + today);
            if (countStr != null) {
                todayCallCount.set(Long.parseLong(countStr));
            }
            log.info("AiClient initialized, provider={}, model={}, todayCallCount={}",
                    provider.getDisplayName(), aiProperties.getModel(), todayCallCount.get());
        } catch (Exception e) {
            log.warn("Failed to load call count from Redis", e);
        }
    }

    private String getCallSource() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            if (!className.startsWith("com.neusoft.health.framework.ai")
                    && !className.startsWith("java.lang.Thread")
                    && !className.startsWith("org.springframework")) {
                return className + "." + element.getMethodName() + ":" + element.getLineNumber();
            }
        }
        return "unknown";
    }

    private boolean checkRateLimit(String source) {
        try {
            long maxPerMinute = aiProperties.getMaxCallsPerMinute() != null
                    ? aiProperties.getMaxCallsPerMinute() : 100;
            long maxPerDay = aiProperties.getMaxCallsPerDay() != null
                    ? aiProperties.getMaxCallsPerDay() : 10000;

            String minuteKey = RATE_LIMIT_KEY_PREFIX + "minute:" + getCurrentMinute();
            Long minuteCount = stringRedisTemplate.opsForValue().increment(minuteKey);
            if (minuteCount == null) minuteCount = 1L;
            if (minuteCount == 1) {
                stringRedisTemplate.expire(minuteKey, 60, java.util.concurrent.TimeUnit.SECONDS);
            }
            if (minuteCount > maxPerMinute) {
                log.error("AI调用超限(每分钟): source={}, count={}, limit={}", source, minuteCount, maxPerMinute);
                return false;
            }

            resetDailyCountIfNeeded();
            long dailyCount = todayCallCount.incrementAndGet();
            if (dailyCount > maxPerDay) {
                todayCallCount.decrementAndGet();
                log.error("AI调用超限(每日): source={}, count={}, limit={}", source, dailyCount, maxPerDay);
                return false;
            }

            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            stringRedisTemplate.opsForValue().set(CALL_COUNT_KEY_PREFIX + today, String.valueOf(dailyCount));

            return true;
        } catch (Exception e) {
            log.warn("Rate limit check failed, proceeding without limit", e);
            return true;
        }
    }

    private void resetDailyCountIfNeeded() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        if (!today.equals(lastResetDate)) {
            synchronized (this) {
                if (!today.equals(lastResetDate)) {
                    todayCallCount.set(0);
                    lastResetDate = today;
                    log.info("Daily call count reset for date: {}", today);
                }
            }
        }
    }

    private String getCurrentMinute() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
    }

    public String chat(String systemPrompt, String userMessage) {
        return chat(systemPrompt, userMessage, null);
    }

    public String chat(String systemPrompt, String userMessage, List<Map<String, String>> history) {
        AiProvider provider = aiProperties.getAiProvider();
        String source = getCallSource();
        long startTime = System.currentTimeMillis();

        if (aiProperties.getEnabled() == null || !aiProperties.getEnabled()) {
            log.error("AI调用被拒绝，服务已禁用: provider={}, source={}", provider.getDisplayName(), source);
            return "AI服务暂时维护中，请稍后再试。";
        }

        if (!checkRateLimit(source)) {
            log.error("AI调用被拒绝，超过调用限制: provider={}, source={}", provider.getDisplayName(), source);
            return "AI服务暂时繁忙，请稍后再试。";
        }

        try {
            log.info("AI调用开始: provider={}, model={}, source={}, messageLength={}, historySize={}",
                    provider.getDisplayName(), aiProperties.getModel(), source,
                    userMessage != null ? userMessage.length() : 0,
                    history != null ? history.size() : 0);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiProperties.getModel());
            requestBody.put("max_tokens", aiProperties.getMaxTokens());
            requestBody.put("temperature", aiProperties.getTemperature());

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

            HttpHeaders headers = new HttpHeaders();
            String authValue = provider.getAuthHeaderPrefix() + aiProperties.getApiKey();
            headers.set(provider.getAuthHeaderName(), authValue);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            String chatUrl = aiProperties.resolveChatEndpoint();
            log.debug("AI请求URL: {}", chatUrl);

            @SuppressWarnings("rawtypes")
            var response = restTemplate.postForEntity(chatUrl, request, Map.class);

            long duration = System.currentTimeMillis() - startTime;

            if (response.getBody() != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    @SuppressWarnings("unchecked")
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    if (message != null) {
                        String content = message.get("content");
                        log.info("AI调用成功: provider={}, source={}, duration={}ms, responseLength={}",
                                provider.getDisplayName(), source, duration,
                                content != null ? content.length() : 0);
                        return content;
                    }
                }
            }
            log.warn("AI调用返回空响应: provider={}, source={}, duration={}ms",
                    provider.getDisplayName(), source, duration);
            return null;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("AI调用失败: provider={}, source={}, duration={}ms, error={}",
                    provider.getDisplayName(), source, duration, e.getMessage(), e);
            return "抱歉，AI 服务暂时不可用，请稍后再试。";
        }
    }

    public String chatMultimodal(String systemPrompt, String userText, String imageUrl) {
        return chatMultimodal(systemPrompt, userText, imageUrl, null);
    }

    public String chatMultimodal(String systemPrompt, String userText, String imageUrl,
                                 List<Map<String, Object>> history) {
        AiProvider provider = aiProperties.getAiProvider();
        String source = getCallSource();
        long startTime = System.currentTimeMillis();

        if (aiProperties.getEnabled() == null || !aiProperties.getEnabled()) {
            log.error("多模态AI调用被拒绝，服务已禁用: provider={}, source={}", provider.getDisplayName(), source);
            return "AI服务暂时维护中，请稍后再试。";
        }

        if (!checkRateLimit(source)) {
            log.error("多模态AI调用被拒绝，超过调用限制: provider={}, source={}", provider.getDisplayName(), source);
            return "AI服务暂时繁忙，请稍后再试。";
        }

        try {
            log.info("多模态AI调用开始: provider={}, model={}, source={}, hasImage={}",
                    provider.getDisplayName(), aiProperties.getModel(), source, imageUrl != null);

            List<Map<String, Object>> inputItems = new ArrayList<>();

            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                Map<String, Object> systemItem = new HashMap<>();
                systemItem.put("role", "system");
                systemItem.put("content", List.of(
                        Map.of("type", "input_text", "text", systemPrompt)
                ));
                inputItems.add(systemItem);
            }

            if (history != null) {
                inputItems.addAll(history);
            }

            List<Map<String, Object>> userContent = new ArrayList<>();
            if (imageUrl != null && !imageUrl.isBlank()) {
                userContent.add(Map.of("type", "input_image", "image_url", imageUrl));
            }
            userContent.add(Map.of("type", "input_text", "text", userText));

            Map<String, Object> userItem = new HashMap<>();
            userItem.put("role", "user");
            userItem.put("content", userContent);
            inputItems.add(userItem);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiProperties.getModel());
            requestBody.put("input", inputItems);

            HttpHeaders headers = new HttpHeaders();
            String authValue = provider.getAuthHeaderPrefix() + aiProperties.getApiKey();
            headers.set(provider.getAuthHeaderName(), authValue);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            String responsesUrl = aiProperties.resolveResponsesEndpoint();
            log.debug("多模态AI请求URL: {}", responsesUrl);

            @SuppressWarnings("rawtypes")
            var response = restTemplate.postForEntity(responsesUrl, request, Map.class);

            long duration = System.currentTimeMillis() - startTime;

            if (response.getBody() != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> output = (List<Map<String, Object>>) response.getBody().get("output");
                if (output != null && !output.isEmpty()) {
                    for (Map<String, Object> item : output) {
                        String type = (String) item.get("type");
                        if ("message".equals(type)) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, String>> contentList =
                                    (List<Map<String, String>>) item.get("content");
                            if (contentList != null && !contentList.isEmpty()) {
                                String content = contentList.get(0).get("text");
                                log.info("多模态AI调用成功: provider={}, source={}, duration={}ms, responseLength={}",
                                        provider.getDisplayName(), source, duration,
                                        content != null ? content.length() : 0);
                                return content;
                            }
                        }
                    }
                }
            }
            log.warn("多模态AI调用返回空响应: provider={}, source={}, duration={}ms",
                    provider.getDisplayName(), source, duration);
            return null;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("多模态AI调用失败: provider={}, source={}, duration={}ms, error={}",
                    provider.getDisplayName(), source, duration, e.getMessage(), e);
            return "抱歉，AI 服务暂时不可用，请稍后再试。";
        }
    }

    @SuppressWarnings("unchecked")
    public float[] embedding(String text) {
        AiProvider provider = aiProperties.getAiProvider();
        String source = getCallSource();
        long startTime = System.currentTimeMillis();

        if (aiProperties.getEnabled() == null || !aiProperties.getEnabled()) {
            log.error("Embedding调用被拒绝，服务已禁用: source={}", source);
            return null;
        }

        if (!checkRateLimit(source)) {
            log.error("Embedding调用被拒绝，超过调用限制: source={}", source);
            return null;
        }

        try {
            String embeddingModel = aiProperties.getEmbeddingModel() != null
                    ? aiProperties.getEmbeddingModel() : "deepseek-embedding";

            log.info("Embedding调用开始: model={}, source={}, textLength={}",
                    embeddingModel, source, text != null ? text.length() : 0);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", embeddingModel);
            requestBody.put("input", text);

            HttpHeaders headers = new HttpHeaders();
            String authValue = provider.getAuthHeaderPrefix() + aiProperties.getApiKey();
            headers.set(provider.getAuthHeaderName(), authValue);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            String embeddingUrl = aiProperties.resolveEmbeddingEndpoint();
            log.debug("Embedding请求URL: {}", embeddingUrl);

            Map<String, Object> response = restTemplate.postForObject(embeddingUrl, request, Map.class);

            long duration = System.currentTimeMillis() - startTime;

            if (response != null) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
                if (data != null && !data.isEmpty()) {
                    List<Number> embeddingList = (List<Number>) data.get(0).get("embedding");
                    if (embeddingList != null) {
                        float[] result = new float[embeddingList.size()];
                        for (int i = 0; i < embeddingList.size(); i++) {
                            result[i] = embeddingList.get(i).floatValue();
                        }
                        log.info("Embedding调用成功: source={}, duration={}ms, dimensions={}",
                                source, duration, result.length);
                        return result;
                    }
                }
            }
            log.warn("Embedding调用返回空响应: source={}, duration={}ms", source, duration);
            return null;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Embedding调用失败: source={}, duration={}ms, error={}",
                    source, duration, e.getMessage(), e);
            return null;
        }
    }

    public long getTodayCallCount() {
        resetDailyCountIfNeeded();
        return todayCallCount.get();
    }
}
