package com.neusoft.health.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页查询通用DTO
 * <p>
 * 用于所有需要分页查询的接口，封装了页码和每页条数参数。
 * 默认值：page=1，pageSize=10。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
public class PageQueryDTO {

    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码最小为1")
    private Integer page = 1;

    /**
     * 每页显示条数，范围1-100
     */
    @Min(value = 1, message = "每页最少1条")
    @Max(value = 100, message = "每页最多100条")
    private Integer pageSize = 10;
}
