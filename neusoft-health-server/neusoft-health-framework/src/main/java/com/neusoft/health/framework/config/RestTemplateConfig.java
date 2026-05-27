package com.neusoft.health.framework.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * RestTemplate配置
 * <p>
 * 配置RestTemplate用于HTTP请求，主要用于调用DeepSeek API和短信服务。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 配置RestTemplate
     *
     * @return RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 配置OkHttpClient（用于WebSocket TTS调用）
     *
     * @return OkHttpClient实例
     */
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }
}
