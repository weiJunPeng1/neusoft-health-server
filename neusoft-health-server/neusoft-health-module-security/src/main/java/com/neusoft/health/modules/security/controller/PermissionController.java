package com.neusoft.health.modules.security.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.common.result.ResultCode;
import com.neusoft.health.modules.security.entity.Permission;
import com.neusoft.health.modules.security.service.PermissionService;
import com.neusoft.health.modules.security.vo.PermissionTreeVO;
import com.neusoft.health.modules.security.vo.PermissionVO;
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

@Tag(name = "权限管理", description = "系统权限资源CRUD及树形结构查询接口，需超级管理员或系统管理员权限")
@RestController
@RequestMapping("/api/v1/admin/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('R002', 'R003')")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "获取权限列表", description = "获取系统所有权限资源的扁平列表，包含权限编码、类型、父级ID等信息")
    @GetMapping("/list")
    public R<List<PermissionVO>> listAll() {
        List<Permission> list = permissionService.list();
        List<PermissionVO> voList = list.stream().map(this::toVO).collect(Collectors.toList());
        return R.ok(voList);
    }

    @Operation(summary = "获取权限树", description = "获取系统权限资源的树形结构，按父子层级关系组织，用于权限分配界面")
    @GetMapping("/tree")
    public R<List<PermissionTreeVO>> listTree() {
        List<Permission> all = permissionService.list();
        List<PermissionTreeVO> tree = buildTree(all);
        return R.ok(tree);
    }

    @Operation(summary = "获取权限详情", description = "根据权限ID获取指定权限的详细信息")
    @GetMapping("/{id}")
    public R<PermissionVO> getById(
            @Parameter(description = "权限ID") @PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        if (permission == null) {
            return R.fail(ResultCode.PERMISSION_NOT_FOUND);
        }
        return R.ok(toVO(permission));
    }

    @Operation(summary = "新增权限", description = "创建新的系统权限资源")
    @PostMapping
    public R<Void> create(@Valid @RequestBody Permission permission) {
        permissionService.save(permission);
        return R.ok();
    }

    @Operation(summary = "编辑权限", description = "修改指定权限的编码、名称、类型、排序等基本信息")
    @PutMapping("/{id}")
    public R<Void> update(
            @Parameter(description = "权限ID") @PathVariable Long id,
            @Valid @RequestBody Permission permission) {
        permission.setId(id);
        permissionService.updateById(permission);
        return R.ok();
    }

    @Operation(summary = "删除权限", description = "删除指定的系统权限资源")
    @DeleteMapping("/{id}")
    public R<Void> delete(
            @Parameter(description = "权限ID") @PathVariable Long id) {
        permissionService.removeById(id);
        return R.ok();
    }

    private PermissionVO toVO(Permission permission) {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }

    private List<PermissionTreeVO> buildTree(List<Permission> all) {
        List<PermissionTreeVO> roots = all.stream()
                .filter(p -> p.getParentId() == null || p.getParentId() == 0)
                .map(p -> toTreeVO(p, all))
                .collect(Collectors.toList());
        return roots;
    }

    private PermissionTreeVO toTreeVO(Permission permission, List<Permission> all) {
        PermissionTreeVO vo = new PermissionTreeVO();
        BeanUtils.copyProperties(permission, vo);
        List<PermissionTreeVO> children = all.stream()
                .filter(p -> p.getParentId() != null && p.getParentId().equals(permission.getId()))
                .map(p -> toTreeVO(p, all))
                .collect(Collectors.toList());
        vo.setChildren(children);
        return vo;
    }
}
