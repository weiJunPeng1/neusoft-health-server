package com.neusoft.health.modules.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.common.exception.BusinessException;
import com.neusoft.health.common.result.ResultCode;
import com.neusoft.health.modules.member.dto.AdminGrantDTO;
import com.neusoft.health.modules.member.entity.MemberLevel;
import com.neusoft.health.modules.member.entity.UserMembership;
import com.neusoft.health.modules.member.mapper.MemberLevelMapper;
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
    private final MemberLevelMapper memberLevelMapper;

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

        MemberLevel levelConfig = memberLevelMapper.selectOne(new LambdaQueryWrapper<MemberLevel>()
                .eq(MemberLevel::getLevelCode, level)
                .eq(MemberLevel::getStatus, 1));

        if (levelConfig != null) {
            vo.setLevelName(levelConfig.getLevelName());
            vo.setDailyQuota(levelConfig.getDailyQuota());
            vo.setContextRounds(levelConfig.getContextRounds());
            vo.setAutoSync(levelConfig.getAutoSync() == 1);
            vo.setDeepAnalysis(levelConfig.getDeepAnalysis() == 1);
            vo.setExportEnabled(levelConfig.getExportEnabled() == 1);
        } else {
            vo.setLevelName(getDefaultLevelName(level));
            vo.setDailyQuota(getDefaultDailyQuota(level));
            vo.setContextRounds(getDefaultContextRounds(level));
            vo.setAutoSync(isAutoSync(level));
            vo.setDeepAnalysis(isDeepAnalysis(level));
            vo.setExportEnabled(isExportEnabled(level));
        }
        return vo;
    }

    private String getDefaultLevelName(String level) {
        switch (level) {
            case "L1": return "白银会员";
            case "L2": return "黄金会员";
            case "L3": return "铂金会员";
            default: return "普通用户";
        }
    }

    private int getDefaultDailyQuota(String level) {
        switch (level) {
            case "L1": return 20;
            case "L2": return 50;
            case "L3": return 0;
            default: return 3;
        }
    }

    private int getDefaultContextRounds(String level) {
        switch (level) {
            case "L1": return 15;
            case "L2": return 30;
            case "L3": return 50;
            default: return 5;
        }
    }

    private boolean isAutoSync(String level) {
        return !"L0".equals(level);
    }

    private boolean isDeepAnalysis(String level) {
        return "L3".equals(level);
    }

    private boolean isExportEnabled(String level) {
        return !"L0".equals(level);
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