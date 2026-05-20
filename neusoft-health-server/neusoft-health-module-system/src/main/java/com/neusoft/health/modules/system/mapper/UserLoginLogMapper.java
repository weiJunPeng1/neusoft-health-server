package com.neusoft.health.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.health.modules.system.entity.UserLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户登录日志Mapper
 * <p>
 * 提供用户登录日志数据访问功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Mapper
public interface UserLoginLogMapper extends BaseMapper<UserLoginLog> {
}
