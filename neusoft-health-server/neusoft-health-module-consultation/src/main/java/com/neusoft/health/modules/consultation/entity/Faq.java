package com.neusoft.health.modules.consultation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 常见问题表（faqs）
 * <p>
 * 预设的高频健康咨询问题，用户点击即可快速提问。
 * 每个分类下至少5个问题，支持预设答案和排序。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("faqs")
public class Faq {

    /** 问题ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类ID（关联faq_categories.id） */
    private Long categoryId;

    /** 问题内容（最大500字符） */
    private String question;

    /** 预设答案（可选，不设置时由AI实时回复） */
    private String presetAnswer;

    /** 排序号（数值越小越靠前） */
    private Integer sortOrder;

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
