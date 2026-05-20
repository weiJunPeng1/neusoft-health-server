package com.neusoft.health.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志表（operation_logs）
 * <p>
 * 记录管理员在后台的所有操作行为，包括请求URL、参数、响应结果、执行耗时等。
 * 敏感字段（request_params、response_result）自动脱敏，完整保留用于安全审计。
 * 该表数据不可由管理员删除，只能由超级管理员定期归档清理。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("operation_logs")
public class OperationLog {

    /** 日志ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作用户ID（关联users.id） */
    private Long userId;

    /** 操作用户名（冗余字段，避免JOIN查询） */
    private String username;

    /** 操作类型（如"禁用用户""修改配置"） */
    private String operation;

    /** 操作模块（如"用户管理""系统配置"） */
    private String module;

    /** 操作对象类型（如User/SystemConfig/Faq） */
    private String targetType;

    /** 操作对象ID */
    private String targetId;

    /** 请求方法（GET/POST/PUT/DELETE） */
    private String requestMethod;

    /** 请求URL */
    private String requestUrl;

    /** 请求参数（JSON格式，敏感信息脱敏） */
    private String requestParams;

    /** 响应结果（JSON格式，敏感信息脱敏） */
    private String responseResult;

    /** 操作者IP地址 */
    private String ipAddress;

    /** User-Agent信息 */
    private String userAgent;

    /** 执行耗时（毫秒） */
    private Integer duration;

    /** 执行状态：0=失败，1=成功 */
    private Integer status;

    /** 错误信息（执行失败时记录） */
    private String errorMsg;

    /** 创建时间（自动填充，即操作时间） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 逻辑删除：0=否，1=是 */
    @TableLogic
    private Integer isDeleted;
}
