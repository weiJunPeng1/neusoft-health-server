package com.neusoft.health.modules.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.security.entity.Role;

import java.util.List;

/**
 * 角色服务
 * <p>
 * 提供角色的查询功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface RoleService extends IService<Role> {

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> listUserRoles(Long userId);

    /**
     * 获取所有角色
     *
     * @return 角色列表
     */
    List<Role> listAll();

    /**
     * 根据编码获取角色
     *
     * @param roleCode 角色编码
     * @return 角色实体
     */
    Role getByCode(String roleCode);
}
