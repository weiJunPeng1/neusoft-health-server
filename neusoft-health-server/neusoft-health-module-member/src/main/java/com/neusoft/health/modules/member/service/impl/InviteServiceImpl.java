package com.neusoft.health.modules.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.health.modules.member.service.InviteService;
import com.neusoft.health.modules.member.vo.InviteInfoVO;
import com.neusoft.health.common.exception.BusinessException;
import com.neusoft.health.common.result.ResultCode;
import com.neusoft.health.common.entity.User;
import com.neusoft.health.common.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {

    private final UserMapper userMapper;

    @Override
    public String generateInviteCode(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (user.getInviteCode() != null && !user.getInviteCode().isEmpty()) {
            return user.getInviteCode();
        }
        String code;
        int attempts = 0;
        do {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            Long existing = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getInviteCode, code));
            if (existing == 0) break;
            attempts++;
        } while (attempts < 10);
        if (attempts >= 10) {
            throw new BusinessException(5001, "邀请码生成失败，请重试");
        }
        user.setInviteCode(code);
        userMapper.updateById(user);
        log.info("Generated invite code {} for user {}", code, userId);
        return code;
    }

    @Override
    @Transactional
    public void bindInviteRelation(Long userId, String inviteCode) {
        if (inviteCode == null || inviteCode.isEmpty()) {
            return;
        }
        User newUser = userMapper.selectById(userId);
        if (newUser == null) {
            return;
        }
        if (newUser.getInvitedBy() != null) {
            return;
        }
        User inviter = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getInviteCode, inviteCode));
        if (inviter == null || inviter.getId().equals(userId)) {
            return;
        }
        newUser.setInvitedBy(inviter.getId());
        userMapper.updateById(newUser);
        log.info("User {} bound to inviter {} via code {}", userId, inviter.getId(), inviteCode);
    }

    @Override
    @Transactional
    public void processInviteReward(Long userId, int paidDays) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getInvitedBy() == null) {
            return;
        }
        int rewardDays = Math.max(1, paidDays / 5);
        User inviter = userMapper.selectById(user.getInvitedBy());
        if (inviter == null || "L0".equals(inviter.getMemberLevel())) {
            log.info("Inviter {} is L0, skip reward", user.getInvitedBy());
            return;
        }
        if (inviter.getMemberExpireTime() != null && inviter.getMemberExpireTime().isAfter(java.time.LocalDateTime.now())) {
            inviter.setMemberExpireTime(inviter.getMemberExpireTime().plusDays(rewardDays));
        } else {
            inviter.setMemberExpireTime(java.time.LocalDateTime.now().plusDays(rewardDays));
        }
        userMapper.updateById(inviter);
        log.info("Inviter {} rewarded {} days for user {} purchase", inviter.getId(), rewardDays, userId);
    }

    @Override
    public InviteInfoVO getInviteInfo(Long userId) {
        User user = userMapper.selectById(userId);
        InviteInfoVO vo = new InviteInfoVO();
        if (user == null) return vo;
        String code = user.getInviteCode();
        if (code == null || code.isEmpty()) {
            code = generateInviteCode(userId);
        }
        vo.setInviteCode(code);
        vo.setInviteUrl("https://neusoft-health.com/register?inviteCode=" + code);
        Long invitedCount = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getInvitedBy, userId));
        vo.setInvitedCount(invitedCount != null ? invitedCount.intValue() : 0);
        vo.setRewardDays(0);
        return vo;
    }
}