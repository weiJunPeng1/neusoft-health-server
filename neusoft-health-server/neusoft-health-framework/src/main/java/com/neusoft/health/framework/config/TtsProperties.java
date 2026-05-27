package com.neusoft.health.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tts")
public class TtsProperties {

    private String provider;

    private String appId;

    private String apiKey;

    private String apiSecret;

    private String apiUrl;

    private String voice;

    private Integer speed;

    private Integer volume;

    private Integer pitch;
}
