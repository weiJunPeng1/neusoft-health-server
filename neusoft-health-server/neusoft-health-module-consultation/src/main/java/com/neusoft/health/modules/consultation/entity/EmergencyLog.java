package com.neusoft.health.modules.consultation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 紧急情况日志表（emergency_logs）
 * <p>
 * 当用户输入匹配到紧急关键词（心梗/中风/大出血等）时触发紧急流程，
 * 系统弹出全屏紧急提示并记录本日志，用于安全审计和数据分析。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("emergency_logs")
public class EmergencyLog {

    /** 日志ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（关联users.id） */
    private Long userId;

    /** 消息ID（关联messages.id） */
    private Long messageId;

    /** 匹配到的紧急关键词（如"心梗"） */
    private String keywordMatched;

    /** 处理状态：1=已处理 */
    private Integer handled;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 逻辑删除：0=否，1=是 */
    @TableLogic
    private Integer isDeleted;
}
