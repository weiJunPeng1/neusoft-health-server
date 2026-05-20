package com.neusoft.health.modules.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色表（roles）
 * <p>
 * 基于RBAC模型的角色定义。系统内置4个角色：
 * R001=普通用户、R002=超级管理员、R003=系统管理员、R004=内容审核员。
 * role_code 用于Spring Security鉴权注解（如@PreAuthorize("hasRole('R002')")）。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("roles")
public class Role {

    /** 角色ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色编码：R001=普通用户，R002=超级管理员，R003=系统管理员，R004=内容审核员 */
    private String roleCode;

    /** 角色名称 */
    private String roleName;

    /** 角色描述说明 */
    private String description;

    /** 排序号（数值越小越靠前） */
    private Integer sortOrder;

    /** 状态：0=禁用，1=正常 */
    private Integer status;

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
