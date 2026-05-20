package com.neusoft.health.modules.security.vo;

import lombok.Data;
import java.util.List;

@Data
public class RolePermissionVO {

    private Long roleId;

    private String roleName;

    private List<PermissionVO> permissions;
}
