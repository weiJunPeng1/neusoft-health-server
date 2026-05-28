package com.neusoft.health.framework.config;

import com.neusoft.health.common.enums.AiProvider;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private String provider;

    private String apiKey;

    private String apiUrl;

    private String model;

    private Integer maxTokens;

    private Double temperature;

    private String embeddingModel = "deepseek-embedding";

    private String chatEndpoint;

    private String responsesEndpoint;

    private Boolean enabled = true;

    private Integer maxCallsPerMinute = 100;

    private Integer maxCallsPerDay = 10000;

    public AiProvider getAiProvider() {
        return AiProvider.fromCode(provider);
    }

    public String resolveChatEndpoint() {
        if (chatEndpoint != null && !chatEndpoint.isBlank()) {
            return apiUrl + chatEndpoint;
        }
        if (getAiProvider() == AiProvider.DOUBAO) {
            return apiUrl + "/chat/completions";
        }
        return apiUrl + "/v1/chat/completions";
    }

    public String resolveResponsesEndpoint() {
        if (responsesEndpoint != null && !responsesEndpoint.isBlank()) {
            return apiUrl + responsesEndpoint;
        }
        if (getAiProvider() == AiProvider.DOUBAO) {
            return apiUrl + "/responses";
        }
        return apiUrl + "/v1/responses";
    }

    public String resolveEmbeddingEndpoint() {
        if (getAiProvider() == AiProvider.DOUBAO) {
            return apiUrl + "/embeddings";
        }
        return apiUrl + "/v1/embeddings";
    }
}
