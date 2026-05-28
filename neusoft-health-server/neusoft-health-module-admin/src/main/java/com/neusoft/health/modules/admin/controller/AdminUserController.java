package com.neusoft.health.modules.admin.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.common.annotation.AdminOperation;
import com.neusoft.health.modules.admin.service.AdminUserService;
import com.neusoft.health.modules.system.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理", description = "管理员用户管理接口，支持用户列表查询、详情查看、状态修改和删除操作")
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(summary = "获取用户列表", description = "按关键词分页查询用户列表，支持用户名或昵称模糊搜索")
    @GetMapping("/list")
    public R<List<UserVO>> listUsers(
            @Parameter(description = "搜索关键词，可选") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {
        return R.ok(adminUserService.listAllUsers(keyword, page, size));
    }

    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户的详细信息，包括个人资料和账号信息")
    @GetMapping("/{id}")
    public R<UserVO> getUserDetail(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        return R.ok(adminUserService.getUserDetail(id));
    }

    @Operation(summary = "修改用户状态", description = "启用或禁用指定用户的账号状态")
    @AdminOperation(module = "用户管理", operation = "修改用户状态", targetType = "User", targetIdParam = "#id")
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "状态值：0-禁用，1-启用") @RequestParam Integer status) {
        adminUserService.updateUserStatus(id, status);
        return R.ok();
    }

    @Operation(summary = "删除用户", description = "根据用户ID删除指定用户账号")
    @AdminOperation(module = "用户管理", operation = "删除用户", targetType = "User", targetIdParam = "#id")
    @DeleteMapping("/{id}")
    public R<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        adminUserService.deleteUser(id);
        return R.ok();
    }
}
