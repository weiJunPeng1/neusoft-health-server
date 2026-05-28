package com.neusoft.health.modules.member.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.health.common.mapper.UserMapper;
import com.neusoft.health.common.result.R;
import com.neusoft.health.modules.member.dto.AdminGrantDTO;
import com.neusoft.health.modules.member.entity.UserMembership;
import com.neusoft.health.modules.member.mapper.UserMembershipMapper;
import com.neusoft.health.modules.member.service.MemberService;
import com.neusoft.health.modules.member.vo.MemberUserVO;
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
import java.util.stream.Collectors;

@Tag(name = "会员管理", description = "管理员手动开通/撤销会员")
@RestController
@RequestMapping("/api/v1/admin/member")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('R002', 'R003')")
public class AdminMemberController {

    private final MemberService memberService;
    private final UserMapper userMapper;
    private final UserMembershipMapper userMembershipMapper;

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
    public R<List<MemberUserVO>> getMemberUsers() {
        List<UserMembership> memberships = userMembershipMapper.selectList(
                new LambdaQueryWrapper<UserMembership>()
                        .eq(UserMembership::getStatus, 1)
                        .orderByDesc(UserMembership::getStartTime)
        );
        List<Long> userIds = memberships.stream()
                .map(UserMembership::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = userIds.isEmpty() ? Map.of() :
                userMapper.selectBatchIds(userIds).stream()
                        .collect(Collectors.toMap(User::getId, u -> u));
        List<MemberUserVO> voList = memberships.stream().map(m -> {
            MemberUserVO vo = new MemberUserVO();
            vo.setId(m.getUserId());
            vo.setMemberLevel(m.getLevelCode());
            vo.setStartTime(m.getStartTime());
            vo.setExpireTime(m.getExpireTime());
            vo.setStatus(m.getStatus());
            User user = userMap.get(m.getUserId());
            if (user != null) {
                vo.setNickname(user.getNickname());
            }
            return vo;
        }).collect(Collectors.toList());
        return R.ok(voList);
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