package com.neusoft.health.modules.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.health.modules.admin.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper
 * <p>
 * 提供操作日志数据访问功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
