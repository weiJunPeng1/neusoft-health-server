package com.neusoft.health.modules.security.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserRoleVO {

    private Long userId;

    private List<RoleVO> roles;
}
