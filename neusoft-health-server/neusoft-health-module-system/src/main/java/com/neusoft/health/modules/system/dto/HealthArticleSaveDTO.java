package com.neusoft.health.modules.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 健康资讯保存DTO
 * <p>
 * 用于创建或更新健康资讯
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
public class HealthArticleSaveDTO {

    /**
     * 资讯ID（更新时需要）
     */
    private Long id;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 255, message = "标题最长255个字符")
    private String title;

    /**
     * 摘要
     */
    @Size(max = 512, message = "摘要最长512个字符")
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
}
