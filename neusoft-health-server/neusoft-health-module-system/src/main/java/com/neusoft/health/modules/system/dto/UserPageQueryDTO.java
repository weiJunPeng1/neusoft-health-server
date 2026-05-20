package com.neusoft.health.modules.system.dto;

import com.neusoft.health.common.dto.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询DTO
 * <p>
 * 用于查询用户列表的条件
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQueryDTO extends PageQueryDTO {

    /**
     * 手机号（已脱敏）
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 状态：0=禁用，1=正常
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
