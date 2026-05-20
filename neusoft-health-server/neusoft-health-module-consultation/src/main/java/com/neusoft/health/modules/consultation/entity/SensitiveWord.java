package com.neusoft.health.modules.consultation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 敏感词表（sensitive_words）
 * <p>
 * 存储内容安全过滤用的敏感词库。支持按分类管理（政治/色情/暴力/医疗风险/违法），
 * 在用户输入和AI输出环节进行双重匹配过滤。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("sensitive_words")
public class SensitiveWord {

    /** 敏感词ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 敏感词内容 */
    private String word;

    /** 敏感词分类：政治/色情/暴力/医疗风险/违法 */
    private String category;

    /** 状态：0=禁用，1=正常 */
    private Integer status;

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
