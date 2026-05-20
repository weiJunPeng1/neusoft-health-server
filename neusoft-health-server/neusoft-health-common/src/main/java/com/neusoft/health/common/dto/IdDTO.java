package com.neusoft.health.common.dto;

import lombok.Data;

/**
 * 通用ID参数DTO
 * <p>
 * 用于仅需要传递单个ID参数的接口，例如删除操作。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
public class IdDTO {

    /**
     * 主键ID
     */
    private Long id;
}
