package com.neusoft.health.modules.admin.service;

import com.neusoft.health.modules.system.vo.UserVO;

import java.util.List;

/**
 * 管理员用户管理服务
 * <p>
 * 提供用户列表查询、状态更新、删除等功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface AdminUserService {

    /**
     * 获取用户列表
     *
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 用户列表
     */
    List<UserVO> listAllUsers(String keyword, int page, int size);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详情VO
     */
    UserVO getUserDetail(Long userId);
}
