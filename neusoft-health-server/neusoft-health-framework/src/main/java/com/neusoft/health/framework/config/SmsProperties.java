package com.neusoft.health.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 短信服务配置属性
 * <p>
 * 用于配置短信服务的相关参数，从配置文件中读取。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {

    /**
     * 短信服务API地址
     */
    private String url;

    /**
     * 短信发送时间限制
     */
    private String time;

    /**
     * 发送短信的最小间隔（秒）
     */
    private Integer minIntervalSeconds;

    /**
     * 每日最大发送次数
     */
    private Integer dailyMaxCount;

    /**
     * 回调短信服务发送状态API地址
     */
    private String queryUrl;

    /**
     * 短信服务回调所需平台用户ID
     */
    private String Token;
}
