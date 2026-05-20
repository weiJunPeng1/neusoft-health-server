package com.neusoft.health.modules.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.security.entity.Permission;

import java.util.List;

/**
 * 权限服务
 * <p>
 * 提供权限的查询功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 根据角色ID获取权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> listByRoleId(Long roleId);

    /**
     * 根据用户ID获取权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> listByUserId(Long userId);

    /**
     * 获取权限树
     *
     * @return 权限列表
     */
    List<Permission> listTree();
}
