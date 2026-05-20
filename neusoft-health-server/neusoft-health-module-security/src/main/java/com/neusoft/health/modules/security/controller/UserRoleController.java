package com.neusoft.health.modules.security.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.modules.security.dto.UserRoleAssignDTO;
import com.neusoft.health.modules.security.entity.Role;
import com.neusoft.health.modules.security.service.RoleService;
import com.neusoft.health.modules.security.service.UserRoleService;
import com.neusoft.health.modules.security.vo.RoleVO;
import com.neusoft.health.modules.security.vo.UserRoleVO;
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

@Tag(name = "用户角色分配", description = "用户与角色的关联分配管理接口，需超级管理员或系统管理员权限")
@RestController
@RequestMapping("/api/v1/admin/user-roles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('R002', 'R003')")
public class UserRoleController {

    private final UserRoleService userRoleService;
    private final RoleService roleService;

    @Operation(summary = "分配用户角色", description = "为指定用户分配角色（全量覆盖），用户将拥有所分配角色的所有权限")
    @PutMapping("/assign")
    public R<Void> assignRoles(@Valid @RequestBody UserRoleAssignDTO dto) {
        userRoleService.assignRoles(dto.getUserId(), dto.getRoleIds());
        return R.ok();
    }

    @Operation(summary = "查询用户角色", description = "根据用户ID查询该用户已分配的所有角色信息")
    @GetMapping("/{userId}")
    public R<UserRoleVO> getUserRoles(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        List<Role> roles = roleService.listUserRoles(userId);
        UserRoleVO vo = new UserRoleVO();
        vo.setUserId(userId);
        vo.setRoles(roles.stream().map(this::toRoleVO).collect(Collectors.toList()));
        return R.ok(vo);
    }

    private RoleVO toRoleVO(Role role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}
