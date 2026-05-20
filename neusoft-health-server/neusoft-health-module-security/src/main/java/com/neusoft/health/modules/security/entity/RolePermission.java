package com.neusoft.health.modules.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色权限关联表（role_permissions）
 * <p>
 * 多对多中间表，连接 roles 和 permissions。
 * role_id + perm_id 构成唯一约束。分配权限后用户即可拥有对应操作权限。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("role_permissions")
public class RolePermission {

    /** 关联ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色ID（关联roles.id） */
    private Long roleId;

    /** 权限ID（关联permissions.id） */
    private Long permId;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /** 逻辑删除：0=否，1=是 */
    @TableLogic
    private Integer isDeleted;
}
