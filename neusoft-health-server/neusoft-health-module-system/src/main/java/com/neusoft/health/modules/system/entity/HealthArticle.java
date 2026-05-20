package com.neusoft.health.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 健康资讯表（health_articles）
 * <p>
 * 存储首页轮播展示的健康科普资讯。内容以H5页面形式承载，通过 content_url 跳转访问。
 * 支持排序控制展示顺序。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("health_articles")
public class HealthArticle {

    /** 文章ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文章标题 */
    private String title;

    /** 文章摘要（首页轮播展示） */
    private String summary;

    /** 封面图URL */
    private String coverUrl;

    /** H5详情页URL */
    private String contentUrl;

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
