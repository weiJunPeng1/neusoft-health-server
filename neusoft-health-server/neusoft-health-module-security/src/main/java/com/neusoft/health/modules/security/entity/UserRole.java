package com.neusoft.health.modules.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户角色关联表（user_roles）
 * <p>
 * 多对多中间表，连接 users 和 roles。
 * user_id + role_id 构成唯一约束，确保不重复分配角色。
 * 一个用户可以拥有多个角色（如同时为管理员+审核员）。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("user_roles")
public class UserRole {

    /** 关联ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（关联users.id） */
    private Long userId;

    /** 角色ID（关联roles.id） */
    private Long roleId;

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
