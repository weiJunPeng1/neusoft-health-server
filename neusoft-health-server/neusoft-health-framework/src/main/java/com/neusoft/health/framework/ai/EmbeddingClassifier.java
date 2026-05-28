package com.neusoft.health.framework.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neusoft.health.common.enums.ConsultCategoryEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingClassifier {

    private final AiClient aiClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY_PREFIX = "ai:embedding:category:";
    private static final long CACHE_EXPIRE_HOURS = 24 * 7;

    public ConsultCategoryEnum classify(String text) {
        if (text == null || text.isBlank()) {
            return ConsultCategoryEnum.OTHER;
        }

        try {
            float[] textEmbedding = aiClient.embedding(text);
            if (textEmbedding == null) {
                log.warn("Embedding计算失败，回退到关键词分类");
                return fallbackClassify(text);
            }

            ConsultCategoryEnum bestCategory = ConsultCategoryEnum.OTHER;
            double bestSimilarity = -1.0;

            for (ConsultCategoryEnum category : ConsultCategoryEnum.values()) {
                float[] categoryEmbedding = getCategoryEmbedding(category);
                if (categoryEmbedding == null) {
                    continue;
                }

                double similarity = cosineSimilarity(textEmbedding, categoryEmbedding);
                if (similarity > bestSimilarity) {
                    bestSimilarity = similarity;
                    bestCategory = category;
                }
            }

            log.info("Embedding分类完成: text={}, category={}, similarity={}",
                    text.substring(0, Math.min(50, text.length())), bestCategory.getLabel(), bestSimilarity);

            return bestCategory;
        } catch (Exception e) {
            log.error("Embedding分类异常: {}", e.getMessage(), e);
            return fallbackClassify(text);
        }
    }

    private float[] getCategoryEmbedding(ConsultCategoryEnum category) {
        String cacheKey = CACHE_KEY_PREFIX + category.name();
        try {
            String cached = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return objectMapper.readValue(cached, new TypeReference<float[]>() {});
            }
        } catch (Exception e) {
            log.warn("读取分类Embedding缓存失败: category={}", category.name());
        }

        String representativeText = category.getLabel() + "：" + category.getDescription();
        float[] embedding = aiClient.embedding(representativeText);
        if (embedding == null) {
            return null;
        }

        try {
            String json = objectMapper.writeValueAsString(embedding);
            stringRedisTemplate.opsForValue().set(cacheKey, json, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("缓存分类Embedding失败: category={}", category.name());
        }

        return embedding;
    }

    private double cosineSimilarity(float[] a, float[] b) {
        if (a == null || b == null || a.length != b.length || a.length == 0) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private ConsultCategoryEnum fallbackClassify(String text) {
        String lowerText = text.toLowerCase();

        if (containsAny(lowerText, "感冒", "发烧", "咳嗽", "胃肠", "高血压", "糖尿病", "心脏")) {
            return ConsultCategoryEnum.INTERNAL;
        }
        if (containsAny(lowerText, "外伤", "骨折", "烧伤", "肿块")) {
            return ConsultCategoryEnum.SURGERY;
        }
        if (containsAny(lowerText, "儿童", "宝宝", "小孩", "发育", "疫苗")) {
            return ConsultCategoryEnum.PEDIATRICS;
        }
        if (containsAny(lowerText, "月经", "孕期", "妇科", "怀孕")) {
            return ConsultCategoryEnum.GYNECOLOGY;
        }
        if (containsAny(lowerText, "皮疹", "过敏", "湿疹", "痤疮", "皮肤")) {
            return ConsultCategoryEnum.DERMATOLOGY;
        }
        if (containsAny(lowerText, "视力", "眼睛", "干眼", "近视")) {
            return ConsultCategoryEnum.OPHTHALMOLOGY;
        }
        if (containsAny(lowerText, "腰痛", "颈椎", "关节", "骨科")) {
            return ConsultCategoryEnum.ORTHOPEDICS;
        }
        if (containsAny(lowerText, "头痛", "失眠", "焦虑", "抑郁")) {
            return ConsultCategoryEnum.NEUROLOGY;
        }
        if (containsAny(lowerText, "饮食", "营养", "减肥", "增重")) {
            return ConsultCategoryEnum.NUTRITION;
        }

        return ConsultCategoryEnum.OTHER;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
