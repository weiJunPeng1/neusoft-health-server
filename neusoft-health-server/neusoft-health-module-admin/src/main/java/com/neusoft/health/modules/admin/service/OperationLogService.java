package com.neusoft.health.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.admin.entity.OperationLog;
import com.neusoft.health.modules.admin.vo.OperationLogVO;

import java.util.List;

/**
 * 操作日志服务
 * <p>
 * 提供操作日志的记录、查询等功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 获取操作日志列表
     *
     * @param userId 用户ID
     * @param action 操作类型
     * @param page 页码
     * @param size 每页大小
     * @return 日志列表
     */
    List<OperationLogVO> listLogs(Long userId, String action, int page, int size);

    /**
     * 记录操作日志
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param action 操作类型
     * @param detail 操作详情
     */
    void recordLog(Long userId, String username, String action, String detail);
}
