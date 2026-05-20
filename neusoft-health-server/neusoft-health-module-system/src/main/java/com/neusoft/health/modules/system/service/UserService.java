package com.neusoft.health.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.common.result.PageResult;
import com.neusoft.health.modules.system.dto.UserPageQueryDTO;
import com.neusoft.health.modules.system.dto.UserStatusDTO;
import com.neusoft.health.modules.system.dto.UserUpdateDTO;
import com.neusoft.health.modules.system.entity.User;
import com.neusoft.health.modules.system.vo.UserDetailVO;
import com.neusoft.health.modules.system.vo.UserVO;

/**
 * 用户服务
 * <p>
 * 提供用户信息查询、修改、状态管理等功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface UserService extends IService<User> {

    /**
     * 获取用户个人信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getProfile(Long userId);

    /**
     * 获取用户详细信息
     *
     * @param userId 用户ID
     * @return 用户详细信息
     */
    UserDetailVO getDetailById(Long userId);

    /**
     * 更新用户个人信息
     *
     * @param userId 用户ID
     * @param dto    更新信息请求
     */
    void updateProfile(Long userId, UserUpdateDTO dto);

    /**
     * 分页查询用户列表
     *
     * @param dto 分页查询条件
     * @return 用户列表分页结果
     */
    PageResult<UserVO> pageUsers(UserPageQueryDTO dto);

    /**
     * 切换用户状态
     *
     * @param dto 状态切换请求
     */
    void toggleStatus(UserStatusDTO dto);
}
