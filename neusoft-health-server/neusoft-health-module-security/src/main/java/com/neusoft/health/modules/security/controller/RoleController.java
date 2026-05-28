package com.neusoft.health.modules.security.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.common.result.ResultCode;
import com.neusoft.health.modules.security.dto.RolePermAssignDTO;
import com.neusoft.health.modules.security.entity.Role;
import com.neusoft.health.modules.security.entity.Permission;
import com.neusoft.health.modules.security.service.PermissionService;
import com.neusoft.health.modules.security.service.RolePermissionService;
import com.neusoft.health.modules.security.service.RoleService;
import com.neusoft.health.modules.security.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "角色管理", description = "系统角色CRUD及角色权限分配接口，需超级管理员或系统管理员权限")
@RestController
@RequestMapping("/api/v1/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('R002', 'R003')")
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final RolePermissionService rolePermissionService;

    @Operation(summary = "获取角色列表", description = "获取系统所有角色列表，包含角色编码、名称和状态")
    @GetMapping("/list")
    public R<List<RoleVO>> listAll() {
        List<Role> list = roleService.listAll();
        List<RoleVO> voList = list.stream().map(this::toVO).collect(Collectors.toList());
        return R.ok(voList);
    }

    @Operation(summary = "获取角色详情", description = "根据角色ID获取角色详细信息及其关联的权限ID列表")
    @GetMapping("/{id}")
    public R<RoleVO> getById(
            @Parameter(description = "角色ID") @PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return R.fail(ResultCode.ROLE_NOT_FOUND);
        }
        RoleVO vo = toVO(role);
        List<Permission> perms = permissionService.listByRoleId(id);
        vo.setPermissionIds(perms.stream().map(Permission::getId).collect(Collectors.toList()));
        return R.ok(vo);
    }

    @Operation(summary = "获取用户角色", description = "根据用户ID查询该用户已被分配的所有角色")
    @GetMapping("/user/{userId}")
    public R<List<RoleVO>> listUserRoles(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        List<Role> roles = roleService.listUserRoles(userId);
        List<RoleVO> voList = roles.stream().map(this::toVO).collect(Collectors.toList());
        return R.ok(voList);
    }

    @Operation(summary = "新增角色", description = "创建新的系统角色，需超级管理员权限")
    @PostMapping
    @PreAuthorize("hasRole('R002')")
    public R<Void> create(@Valid @RequestBody Role role) {
        roleService.save(role);
        return R.ok();
    }

    @Operation(summary = "编辑角色", description = "修改指定角色的名称、描述、排序等基本信息，需超级管理员权限")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('R002')")
    public R<Void> update(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Valid @RequestBody Role role) {
        role.setId(id);
        roleService.updateById(role);
        return R.ok();
    }

    @Operation(summary = "删除角色", description = "删除指定系统角色，需超级管理员权限")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('R002')")
    public R<Void> delete(
            @Parameter(description = "角色ID") @PathVariable Long id) {
        roleService.removeById(id);
        return R.ok();
    }

    @Operation(summary = "分配角色权限", description = "为指定角色重新分配权限（全量覆盖），需管理员权限")
    @PutMapping("/{id}/permissions")
    public R<Void> assignPermissions(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Valid @RequestBody RolePermAssignDTO dto) {
        dto.setRoleId(id);
        rolePermissionService.assignPermissions(dto.getRoleId(), dto.getPermIds());
        return R.ok();
    }

    private RoleVO toVO(Role role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}
