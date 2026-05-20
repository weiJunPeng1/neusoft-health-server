package com.neusoft.health.modules.system.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 健康资讯VO
 * <p>
 * 用于展示健康资讯信息
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
public class HealthArticleVO {

    /**
     * 资讯ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 封面URL
     */
    private String coverUrl;

    /**
     * 内容URL
     */
    private String contentUrl;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态：0=禁用，1=正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}
