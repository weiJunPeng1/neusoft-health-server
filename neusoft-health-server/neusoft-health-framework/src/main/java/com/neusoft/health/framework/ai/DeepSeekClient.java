package com.neusoft.health.framework.ai;

import com.neusoft.health.framework.config.DeepSeekProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

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
     * Redis模板，用于调用统计和限流
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 当日调用计数器（内存缓存）
     */
    private final AtomicLong todayCallCount = new AtomicLong(0);
    /**
     * 最后重置日期
     */
    private volatile String lastResetDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

    /**
     * 调用统计Redis键前缀
     */
    private static final String CALL_COUNT_KEY_PREFIX = "ai:call_count:";
    /**
     * 限流Redis键前缀
     */
    private static final String RATE_LIMIT_KEY_PREFIX = "ai:rate_limit:";

    /**
     * 初始化方法：从Redis加载今日调用计数
     */
    @PostConstruct
    public void init() {
        try {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String countStr = stringRedisTemplate.opsForValue().get(CALL_COUNT_KEY_PREFIX + today);
            if (countStr != null) {
                todayCallCount.set(Long.parseLong(countStr));
            }
            log.info("DeepSeekClient initialized, today call count: {}", todayCallCount.get());
        } catch (Exception e) {
            log.warn("Failed to load call count from Redis", e);
        }
    }

    /**
     * 获取调用来源信息（堆栈分析）
     */
    private String getCallSource() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            // 跳过本类和Java核心类
            if (!className.startsWith("com.neusoft.health.framework.ai") &&
                !className.startsWith("java.lang.Thread") &&
                !className.startsWith("org.springframework")) {
                return className + "." + element.getMethodName() + ":" + element.getLineNumber();
            }
        }
        return "unknown";
    }

    /**
     * 检查是否超过调用限制
     */
    private boolean checkRateLimit(String source) {
        try {
            // 从配置获取限制值
            long maxPerMinute = deepSeekProperties.getMaxCallsPerMinute() != null 
                    ? deepSeekProperties.getMaxCallsPerMinute() : 100;
            long maxPerDay = deepSeekProperties.getMaxCallsPerDay() != null 
                    ? deepSeekProperties.getMaxCallsPerDay() : 10000;

            // 检查每分钟调用限制
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

            // 检查每日调用限制
            resetDailyCountIfNeeded();
            long dailyCount = todayCallCount.incrementAndGet();
            if (dailyCount > maxPerDay) {
                todayCallCount.decrementAndGet();
                log.error("AI调用超限(每日): source={}, count={}, limit={}", source, dailyCount, maxPerDay);
                return false;
            }

            // 更新Redis中的每日计数
            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            stringRedisTemplate.opsForValue().set(CALL_COUNT_KEY_PREFIX + today, String.valueOf(dailyCount));

            return true;
        } catch (Exception e) {
            log.warn("Rate limit check failed, proceeding without limit", e);
            return true;
        }
    }

    /**
     * 重置每日计数器（线程安全）
     */
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

    /**
     * 获取当前分钟（用于限流key）
     */
    private String getCurrentMinute() {
        return java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
    }

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
        // 获取调用来源
        String source = getCallSource();
        long startTime = System.currentTimeMillis();

        // 检查AI服务是否启用
        if (deepSeekProperties.getEnabled() == null || !deepSeekProperties.getEnabled()) {
            log.error("AI调用被拒绝，服务已禁用: source={}", source);
            return "AI服务暂时维护中，请稍后再试。";
        }

        // 检查调用限制
        if (!checkRateLimit(source)) {
            log.error("AI调用被拒绝，超过调用限制: source={}", source);
            return "AI服务暂时繁忙，请稍后再试。";
        }

        try {
            log.info("AI调用开始: source={}, messageLength={}, historySize={}", 
                    source, 
                    userMessage != null ? userMessage.length() : 0,
                    history != null ? history.size() : 0);

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

            long duration = System.currentTimeMillis() - startTime;

            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    if (message != null) {
                        String content = message.get("content");
                        log.info("AI调用成功: source={}, duration={}ms, responseLength={}", 
                                source, duration, content != null ? content.length() : 0);
                        return content;
                    }
                }
            }
            log.warn("AI调用返回空响应: source={}, duration={}ms", source, duration);
            return null;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("AI调用失败: source={}, duration={}ms, error={}", source, duration, e.getMessage(), e);
            return "抱歉，AI 服务暂时不可用，请稍后再试。";
        }
    }

    /**
     * 获取今日调用次数
     */
    public long getTodayCallCount() {
        resetDailyCountIfNeeded();
        return todayCallCount.get();
    }
}
