package com.neusoft.health.modules.consultation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 咨询会话表（sessions）
 * <p>
 * 每个会话包含多轮对话消息。会话标题取首条问题前20字，
 * message_count 记录消息总数，last_message_at 用于排序。
 * 用户删除会话后触发逻辑删除，数据保留用于审核。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("sessions")
public class Session {

    /** 会话ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（关联users.id） */
    private Long userId;

    /** 会话标题（截取首条问题前20字） */
    private String title;

    /** 消息数量（含用户消息和AI回复） */
    private Integer messageCount;

    /** 最后一条消息的发送时间 */
    private LocalDateTime lastMessageAt;

    /** 会话状态：0=已结束，1=进行中 */
    private Integer status;

    /** 咨询分类（由AI自动识别：内科/外科/儿科/...） */
    private String category;

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
