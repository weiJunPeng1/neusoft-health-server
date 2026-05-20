package com.neusoft.health.modules.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.common.exception.BusinessException;
import com.neusoft.health.common.result.ResultCode;
import com.neusoft.health.modules.member.dto.AdminGrantDTO;
import com.neusoft.health.modules.member.entity.UserMembership;
import com.neusoft.health.modules.member.mapper.UserMembershipMapper;
import com.neusoft.health.modules.member.service.MemberService;
import com.neusoft.health.modules.member.vo.MemberStatusVO;
import com.neusoft.health.modules.system.entity.User;
import com.neusoft.health.modules.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl extends ServiceImpl<UserMembershipMapper, UserMembership> implements MemberService {

    private final UserMapper userMapper;

    private static final int GRACE_HOURS = 24;

    @Override
    public MemberStatusVO getMemberStatus(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        MemberStatusVO vo = new MemberStatusVO();
        String level = user.getMemberLevel() != null ? user.getMemberLevel() : "L0";
        vo.setLevelCode(level);
        vo.setExpireTime(user.getMemberExpireTime());

        if (user.getMemberExpireTime() != null) {
            long remaining = ChronoUnit.DAYS.between(LocalDateTime.now(), user.getMemberExpireTime());
            vo.setRemainingDays((int) Math.max(0, remaining));
            LocalDateTime graceEnd = user.getMemberExpireTime().plusHours(GRACE_HOURS);
            vo.setInGracePeriod(LocalDateTime.now().isBefore(graceEnd) && LocalDateTime.now().isAfter(user.getMemberExpireTime()));
        } else {
            vo.setRemainingDays(0);
            vo.setInGracePeriod(false);
        }

        switch (level) {
            case "L0":
                vo.setLevelName("普通用户"); vo.setDailyQuota(3); vo.setContextRounds(5);
                vo.setAutoSync(false); vo.setDeepAnalysis(false); vo.setExportEnabled(false);
                break;
            case "L1":
                vo.setLevelName("白银会员"); vo.setDailyQuota(20); vo.setContextRounds(15);
                vo.setAutoSync(true); vo.setDeepAnalysis(false); vo.setExportEnabled(true);
                break;
            case "L2":
                vo.setLevelName("黄金会员"); vo.setDailyQuota(50); vo.setContextRounds(30);
                vo.setAutoSync(true); vo.setDeepAnalysis(false); vo.setExportEnabled(true);
                break;
            case "L3":
                vo.setLevelName("铂金会员"); vo.setDailyQuota(0); vo.setContextRounds(50);
                vo.setAutoSync(true); vo.setDeepAnalysis(true); vo.setExportEnabled(true);
                break;
        }
        return vo;
    }

    @Override
    @Transactional
    public void grantMembership(AdminGrantDTO dto) {
        User user = userMapper.selectById(dto.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        activateMembership(dto.getUserId(), dto.getLevelCode(), dto.getDurationDays(), null);
        log.info("Admin granted {} membership {} for {} days, reason: {}", dto.getUserId(), dto.getLevelCode(), dto.getDurationDays(), dto.getReason());
    }

    @Override
    @Transactional
    public void revokeMembership(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setMemberLevel("L0");
        user.setMemberExpireTime(null);
        userMapper.updateById(user);
        log.info("Membership revoked for user {}", userId);
    }

    @Override
    @Transactional
    public void activateMembership(Long userId, String levelCode, int durationDays, Long orderId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime;
        if (user.getMemberExpireTime() != null && user.getMemberExpireTime().isAfter(now)) {
            expireTime = user.getMemberExpireTime().plusDays(durationDays);
        } else {
            expireTime = now.plusDays(durationDays);
        }
        user.setMemberLevel(levelCode);
        user.setMemberExpireTime(expireTime);
        user.setFirstPurchase(1);
        userMapper.updateById(user);

        UserMembership membership = new UserMembership();
        membership.setUserId(userId);
        membership.setLevelCode(levelCode);
        membership.setStartTime(now);
        membership.setExpireTime(expireTime);
        membership.setSourceOrderId(orderId);
        membership.setStatus(1);
        save(membership);
        log.info("Membership activated: userId={}, level={}, days={}, expire={}", userId, levelCode, durationDays, expireTime);
    }

    @Override
    public List<UserMembership> getMembershipHistory(Long userId) {
        return list(new LambdaQueryWrapper<UserMembership>()
                .eq(UserMembership::getUserId, userId)
                .orderByDesc(UserMembership::getCreatedTime));
    }
}