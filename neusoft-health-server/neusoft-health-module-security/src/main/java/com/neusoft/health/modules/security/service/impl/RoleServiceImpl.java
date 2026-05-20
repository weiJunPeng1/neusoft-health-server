package com.neusoft.health.modules.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.modules.security.entity.Role;
import com.neusoft.health.modules.security.entity.UserRole;
import com.neusoft.health.modules.security.mapper.RoleMapper;
import com.neusoft.health.modules.security.service.RoleService;
import com.neusoft.health.modules.security.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final UserRoleService userRoleService;

    @Override
    public List<Role> listUserRoles(Long userId) {
        List<UserRole> userRoles = userRoleService.listByUserId(userId);
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return list(new LambdaQueryWrapper<Role>().in(Role::getId, roleIds));
    }

    @Override
    public List<Role> listAll() {
        return list(new LambdaQueryWrapper<Role>().orderByAsc(Role::getSortOrder));
    }

    @Override
    public Role getByCode(String roleCode) {
        return getOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, roleCode));
    }
}
