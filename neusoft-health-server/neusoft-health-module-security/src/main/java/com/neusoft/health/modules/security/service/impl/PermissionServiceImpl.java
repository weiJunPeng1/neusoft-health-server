package com.neusoft.health.modules.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.modules.security.entity.Permission;
import com.neusoft.health.modules.security.entity.RolePermission;
import com.neusoft.health.modules.security.entity.UserRole;
import com.neusoft.health.modules.security.mapper.PermissionMapper;
import com.neusoft.health.modules.security.service.PermissionService;
import com.neusoft.health.modules.security.service.RolePermissionService;
import com.neusoft.health.modules.security.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final RolePermissionService rolePermissionService;
    private final UserRoleService userRoleService;

    @Override
    public List<Permission> listByRoleId(Long roleId) {
        List<RolePermission> rps = rolePermissionService.listByRoleId(roleId);
        List<Long> permIds = rps.stream().map(RolePermission::getPermId).collect(Collectors.toList());
        if (permIds.isEmpty()) {
            return List.of();
        }
        return list(new LambdaQueryWrapper<Permission>().in(Permission::getId, permIds));
    }

    @Override
    public List<Permission> listByUserId(Long userId) {
        List<UserRole> userRoles = userRoleService.listByUserId(userId);
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return List.of();
        }
        List<RolePermission> rps = rolePermissionService.list(
                new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId, roleIds));
        List<Long> permIds = rps.stream().map(RolePermission::getPermId).distinct().collect(Collectors.toList());
        if (permIds.isEmpty()) {
            return List.of();
        }
        return list(new LambdaQueryWrapper<Permission>().in(Permission::getId, permIds));
    }

    @Override
    public List<Permission> listTree() {
        List<Permission> all = list(new LambdaQueryWrapper<Permission>().orderByAsc(Permission::getSortOrder));
        List<Permission> tree = new ArrayList<>();
        for (Permission p : all) {
            if (p.getParentId() == null || p.getParentId() == 0) {
                tree.add(p);
            }
        }
        for (Permission parent : tree) {
            List<Permission> children = all.stream()
                    .filter(p -> p.getParentId() != null && p.getParentId().equals(parent.getId()))
                    .collect(Collectors.toList());
            parent.setChildren(children);
        }
        return tree;
    }
}
