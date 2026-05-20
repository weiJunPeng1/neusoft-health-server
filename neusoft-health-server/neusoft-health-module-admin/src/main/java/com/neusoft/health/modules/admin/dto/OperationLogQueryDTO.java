package com.neusoft.health.modules.admin.dto;

import com.neusoft.health.common.dto.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志查询DTO
 * <p>
 * 用于查询操作日志的条件
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OperationLogQueryDTO extends PageQueryDTO {

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 执行状态：0=失败，1=成功
     */
    private Integer status;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;
}
