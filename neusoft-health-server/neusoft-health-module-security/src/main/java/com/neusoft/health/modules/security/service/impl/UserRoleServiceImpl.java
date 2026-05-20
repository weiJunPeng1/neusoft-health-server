package com.neusoft.health.modules.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.modules.security.entity.UserRole;
import com.neusoft.health.modules.security.mapper.UserRoleMapper;
import com.neusoft.health.modules.security.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    public List<UserRole> listByUserId(Long userId) {
        return list(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
    }

    @Override
    public void assignRoles(Long userId, List<Long> roleIds) {
        removeByUserId(userId);
        for (Long roleId : roleIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            save(ur);
        }
    }

    @Override
    public void removeByUserId(Long userId) {
        remove(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
    }
}
