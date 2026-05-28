package com.neusoft.health.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tts")
public class TtsProperties {

    private String provider;

    private String apiKey;

    private String appId;

    private String accessToken;

    private String secretKey;

    private String resourceId;

    private String speaker;

    private Integer speed;

    private Integer volume;

    private String format = "mp3";

    private Integer sampleRate = 24000;
}
