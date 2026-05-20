package com.neusoft.health.modules.security.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PermissionVO {

    private Long id;

    private String permCode;

    private String permName;

    private String permType;

    private Long parentId;

    private Integer sortOrder;

    private LocalDateTime createdTime;
}
