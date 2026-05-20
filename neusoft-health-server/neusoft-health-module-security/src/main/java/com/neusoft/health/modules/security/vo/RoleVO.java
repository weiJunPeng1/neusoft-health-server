package com.neusoft.health.modules.security.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleVO {

    private Long id;

    private String roleCode;

    private String roleName;

    private String description;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createdTime;

    private List<Long> permissionIds;
}
