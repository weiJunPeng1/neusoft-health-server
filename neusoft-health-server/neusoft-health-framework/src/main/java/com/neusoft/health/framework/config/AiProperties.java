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

    private Boolean enabled = true;

    private Integer maxCallsPerMinute = 100;

    private Integer maxCallsPerDay = 10000;

    public AiProvider getAiProvider() {
        return AiProvider.fromCode(provider);
    }
}
