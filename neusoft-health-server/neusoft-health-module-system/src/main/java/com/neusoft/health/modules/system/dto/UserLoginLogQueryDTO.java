package com.neusoft.health.modules.system.dto;

import com.neusoft.health.common.dto.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户登录日志查询DTO
 * <p>
 * 用于查询用户登录日志的条件
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserLoginLogQueryDTO extends PageQueryDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录结果：0=失败，1=成功
     */
    private Integer loginResult;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;
}
