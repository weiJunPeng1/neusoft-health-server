package com.neusoft.health.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DeepSeek AI配置属性
 * <p>
 * 用于配置DeepSeek API的相关参数，从配置文件中读取。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekProperties {

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * API地址
     */
    private String apiUrl;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 最大生成令牌数
     */
    private Integer maxTokens;

    /**
     * 温度参数，控制生成文本的随机性
     */
    private Double temperature;
}
