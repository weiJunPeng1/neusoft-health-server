package com.neusoft.health.modules.admin.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志VO
 * <p>
 * 用于展示操作日志信息
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
public class OperationLogVO {

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 操作用户名
     */
    private String username;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作对象类型
     */
    private String targetType;

    /**
     * 操作对象ID
     */
    private String targetId;

    /**
     * 请求方法（GET/POST/PUT/DELETE）
     */
    private String requestMethod;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求参数（JSON格式，敏感信息脱敏）
     */
    private String requestParams;

    /**
     * 响应结果（JSON格式，敏感信息脱敏）
     */
    private String responseResult;

    /**
     * 操作者IP地址
     */
    private String ipAddress;

    /**
     * User-Agent信息
     */
    private String userAgent;

    /**
     * 执行耗时（毫秒）
     */
    private Integer duration;

    /**
     * 执行状态：0=失败，1=成功
     */
    private Integer status;

    /**
     * 错误信息（执行失败时记录）
     */
    private String errorMsg;

    /**
     * 创建时间（操作时间）
     */
    private LocalDateTime createdTime;
}
