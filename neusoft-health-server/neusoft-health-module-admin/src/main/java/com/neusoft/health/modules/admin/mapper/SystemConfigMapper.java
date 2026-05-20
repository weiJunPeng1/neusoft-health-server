package com.neusoft.health.modules.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.health.modules.admin.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置Mapper
 * <p>
 * 提供系统配置数据访问功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {
}
