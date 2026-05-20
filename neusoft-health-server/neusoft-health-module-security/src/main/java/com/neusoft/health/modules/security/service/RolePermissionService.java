package com.neusoft.health.modules.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.security.entity.RolePermission;

import java.util.List;

public interface RolePermissionService extends IService<RolePermission> {

    List<RolePermission> listByRoleId(Long roleId);

    void assignPermissions(Long roleId, List<Long> permIds);
}
