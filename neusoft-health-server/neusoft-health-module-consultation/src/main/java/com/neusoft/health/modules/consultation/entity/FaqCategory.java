package com.neusoft.health.modules.consultation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * FAQ分类表（faq_categories）
 * <p>
 * 常见问题的分类，如感冒发烧、肠胃不适、睡眠问题等6个分类。
 * 支持排序控制和启用/禁用管理。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("faq_categories")
public class FaqCategory {

    /** 分类ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类名称（如"感冒发烧""睡眠问题"） */
    private String name;

    /** 分类图标URL */
    private String icon;

    /** 排序号（数值越小越靠前） */
    private Integer sortOrder;

    /** 状态：0=禁用（隐藏），1=正常（展示） */
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
