package com.neusoft.health.modules.member.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.health.common.mapper.UserMapper;
import com.neusoft.health.common.result.R;
import com.neusoft.health.modules.member.dto.AdminGrantDTO;
import com.neusoft.health.modules.member.entity.UserMembership;
import com.neusoft.health.modules.member.service.MemberService;
import com.neusoft.health.common.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "会员管理", description = "管理员手动开通/撤销会员")
@RestController
@RequestMapping("/api/v1/admin/member")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('R002', 'R003')")
public class AdminMemberController {

    private final MemberService memberService;
    private final UserMapper userMapper;

    @Operation(summary = "手动开通会员（R002）")
    @PostMapping("/grant")
    @PreAuthorize("hasRole('R002')")
    public R<Void> grant(@Valid @RequestBody AdminGrantDTO dto) {
        memberService.grantMembership(dto);
        return R.ok();
    }

    @Operation(summary = "撤销会员（R002）")
    @PostMapping("/revoke")
    @PreAuthorize("hasRole('R002')")
    public R<Void> revoke(@RequestParam Long userId) {
        memberService.revokeMembership(userId);
        return R.ok();
    }

    @Operation(summary = "获取会员用户列表")
    @GetMapping("/users")
    public R<List<User>> getMemberUsers() {
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .ne(User::getMemberLevel, "L0")
                        .orderByDesc(User::getUpdatedTime)
        );
        return R.ok(users);
    }

    @Operation(summary = "获取会员统计数据")
    @GetMapping("/stats")
    public R<Map<String, Object>> getMemberStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalUsers = userMapper.selectCount(null);
        
        long l1Count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getMemberLevel, "L1")
        );
        long l2Count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getMemberLevel, "L2")
        );
        long l3Count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getMemberLevel, "L3")
        );
        
        stats.put("totalUsers", totalUsers);
        stats.put("l1Count", l1Count);
        stats.put("l2Count", l2Count);
        stats.put("l3Count", l3Count);
        stats.put("totalMemberCount", l1Count + l2Count + l3Count);
        
        return R.ok(stats);
    }
}