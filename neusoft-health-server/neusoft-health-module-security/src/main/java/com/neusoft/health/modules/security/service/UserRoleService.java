package com.neusoft.health.modules.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.security.entity.UserRole;

import java.util.List;

public interface UserRoleService extends IService<UserRole> {

    List<UserRole> listByUserId(Long userId);

    void assignRoles(Long userId, List<Long> roleIds);

    void removeByUserId(Long userId);
}
