package com.neusoft.health.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.system.dto.UserLoginLogQueryDTO;
import com.neusoft.health.modules.system.entity.UserLoginLog;
import com.neusoft.health.modules.system.vo.UserLoginLogVO;

import java.util.List;

/**
 * 用户登录日志服务
 */
public interface UserLoginLogService extends IService<UserLoginLog> {

    /**
     * 查询当前用户的登录历史
     *
     * @param userId 用户ID
     * @param dto    查询条件
     * @return 登录日志列表
     */
    List<UserLoginLogVO> getUserLoginLogs(Long userId, UserLoginLogQueryDTO dto);
}
