package com.neusoft.health.modules.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限表（permissions）
 * <p>
 * 定义系统所有功能权限点，支持三级粒度：
 * menu=菜单权限（控制菜单可见性），button=按钮权限（控制操作按钮），data=数据权限（控制数据范围）。
 * 通过parent_id构建树形层级结构，前台全部权限 parent_id=0，子权限挂载到对应菜单下。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("permissions")
public class Permission {

    /** 权限ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 权限编码（如admin:user:list、user:consultation:send） */
    private String permCode;

    /** 权限名称 */
    private String permName;

    /** 权限类型：menu=菜单，button=按钮，data=数据 */
    private String permType;

    /** 父权限ID（0表示根权限） */
    private Long parentId;

    /** 排序号（数值越小越靠前） */
    private Integer sortOrder;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /** 逻辑删除：0=否，1=是 */
    @TableLogic
    private Integer isDeleted;

    /** 子权限列表（非数据库字段） */
    @TableField(exist = false)
    private List<Permission> children;
}
