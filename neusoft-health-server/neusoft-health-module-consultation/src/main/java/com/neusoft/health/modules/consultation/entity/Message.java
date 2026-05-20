package com.neusoft.health.modules.consultation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 咨询消息表（messages）
 * <p>
 * 存储每轮对话的单条消息（用户消息/AI回复/系统消息）。
 * 集成审核流程字段（review_status、reviewed_by、modified_content），
 * 集成紧急情况标记（is_emergency、emergency_keyword），
 * 集成合规审核字段（is_violation、violation_reason）。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("messages")
public class Message {

    /** 消息ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会话ID（关联sessions.id） */
    private Long sessionId;

    /** 用户ID（关联users.id） */
    private Long userId;

    /** 消息角色：user=用户，assistant=AI助手，system=系统 */
    private String role;

    /** 消息正文内容 */
    private String content;

    /** 内容类型：1=文本 */
    private Integer contentType;

    /** 是否触发紧急流程：0=否，1=是 */
    private Integer isEmergency;

    /** 触发的紧急关键词（如"心梗""中风"） */
    private String emergencyKeyword;

    /** 是否违规：0=否，1=是 */
    private Integer isViolation;

    /** 违规原因描述 */
    private String violationReason;

    /** 审核状态：0=待审核，1=通过，2=违规 */
    private Integer reviewStatus;

    /** 审核人ID（关联users.id） */
    private Long reviewedBy;

    /** 审核时间 */
    private LocalDateTime reviewedAt;

    /** 人工修改后的内容（审核员修改AI回复） */
    private String modifiedContent;

    /** DeepSeek API调用耗时（毫秒） */
    private Integer apiCallDuration;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /** 逻辑删除：0=否，1=是 */
    @TableLogic
    private Integer isDeleted;
}
