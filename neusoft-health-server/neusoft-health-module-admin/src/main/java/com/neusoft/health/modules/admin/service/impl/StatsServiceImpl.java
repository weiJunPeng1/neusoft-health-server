package com.neusoft.health.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.health.modules.admin.service.StatsService;
import com.neusoft.health.common.enums.ReviewStatusEnum;
import com.neusoft.health.modules.consultation.entity.Message;
import com.neusoft.health.modules.consultation.entity.Session;
import com.neusoft.health.modules.consultation.mapper.MessageMapper;
import com.neusoft.health.modules.consultation.mapper.SessionMapper;
import com.neusoft.health.modules.system.entity.User;
import com.neusoft.health.modules.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final UserMapper userMapper;
    private final SessionMapper sessionMapper;
    private final MessageMapper messageMapper;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userMapper.selectCount(null);
        long totalSessions = sessionMapper.selectCount(null);
        long totalMessages = messageMapper.selectCount(null);

        long pendingReviews = messageMapper.selectCount(new LambdaQueryWrapper<Message>()
                .eq(Message::getReviewStatus, ReviewStatusEnum.PENDING.getCode()));

        long emergencyCount = messageMapper.selectCount(new LambdaQueryWrapper<Message>()
                .eq(Message::getIsEmergency, 1));

        stats.put("totalUsers", totalUsers);
        stats.put("totalSessions", totalSessions);
        stats.put("totalMessages", totalMessages);
        stats.put("pendingReviews", pendingReviews);
        stats.put("emergencyCount", emergencyCount);

        return stats;
    }
}
