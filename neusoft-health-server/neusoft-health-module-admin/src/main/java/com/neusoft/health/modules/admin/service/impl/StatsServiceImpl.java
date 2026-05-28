package com.neusoft.health.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.health.common.enums.ReviewStatusEnum;
import com.neusoft.health.modules.admin.service.StatsService;
import com.neusoft.health.modules.consultation.entity.Message;
import com.neusoft.health.modules.consultation.entity.Session;
import com.neusoft.health.modules.consultation.entity.HealthSearchCache;
import com.neusoft.health.modules.consultation.mapper.HealthSearchCacheMapper;
import com.neusoft.health.modules.consultation.mapper.MessageMapper;
import com.neusoft.health.modules.consultation.mapper.SessionMapper;
import com.neusoft.health.common.entity.User;
import com.neusoft.health.common.mapper.UserMapper;
import com.neusoft.health.modules.member.entity.UserMembership;
import com.neusoft.health.modules.member.mapper.UserMembershipMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final UserMapper userMapper;
    private final SessionMapper sessionMapper;
    private final MessageMapper messageMapper;
    private final UserMembershipMapper userMembershipMapper;
    private final HealthSearchCacheMapper healthSearchCacheMapper;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(today, LocalTime.MAX);

        long totalUsers = userMapper.selectCount(null);
        long todayUsers = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .ge(User::getCreatedTime, todayStart)
                .le(User::getCreatedTime, todayEnd));

        long totalSessions = sessionMapper.selectCount(null);
        long totalMessages = messageMapper.selectCount(null);

        long todayMessages = messageMapper.selectCount(new LambdaQueryWrapper<Message>()
                .ge(Message::getCreatedTime, todayStart)
                .le(Message::getCreatedTime, todayEnd));

        long pendingReviews = messageMapper.selectCount(new LambdaQueryWrapper<Message>()
                .eq(Message::getReviewStatus, ReviewStatusEnum.PENDING.getCode()));

        long emergencyCount = messageMapper.selectCount(new LambdaQueryWrapper<Message>()
                .eq(Message::getIsEmergency, 1));

        long todaySessions = sessionMapper.selectCount(new LambdaQueryWrapper<Session>()
                .ge(Session::getCreatedTime, todayStart)
                .le(Session::getCreatedTime, todayEnd));

        stats.put("totalUsers", totalUsers);
        stats.put("todayUsers", todayUsers);
        stats.put("totalSessions", totalSessions);
        stats.put("totalMessages", totalMessages);
        stats.put("todayMessages", todayMessages);
        stats.put("todaySessions", todaySessions);
        stats.put("pendingReviews", pendingReviews);
        stats.put("emergencyCount", emergencyCount);
        stats.put("categoryStats", getCategoryStats());
        stats.put("memberStats", getMemberStats());
        stats.put("hotConsults", getHotConsults(8));
        stats.put("recentConsults", getRecentConsults(20));

        return stats;
    }

    @Override
    public List<Map<String, Object>> getCategoryStats() {
        List<Session> sessions = sessionMapper.selectList(null);

        Map<String, Long> countMap = new HashMap<>();
        for (Session session : sessions) {
            String category = session.getCategory();
            if (category == null || category.isEmpty()) {
                category = inferCategory(session.getTitle());
            }
            if (category == null || category.isEmpty()) {
                category = "其他";
            }
            countMap.merge(category, 1L, Long::sum);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        countMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", e.getKey());
                    item.put("value", e.getValue());
                    result.add(item);
                });

        return result;
    }

    private String inferCategory(String title) {
        if (title == null || title.isEmpty()) return null;
        String t = title;
        if (t.contains("感冒") || t.contains("发烧") || t.contains("咳嗽") || t.contains("鼻炎") || t.contains("咽炎") || t.contains("流感")) return "感冒发烧";
        if (t.contains("胃痛") || t.contains("胃炎") || t.contains("肠炎") || t.contains("拉肚子") || t.contains("腹泻") || t.contains("消化不良") || t.contains("便秘")) return "肠胃不适";
        if (t.contains("失眠") || t.contains("睡眠") || t.contains("熬夜") || t.contains("打鼾") || t.contains("睡不着")) return "睡眠问题";
        if (t.contains("运动") || t.contains("跑步") || t.contains("健身") || t.contains("膝盖") || t.contains("肌肉") || t.contains("扭伤")) return "运动健康";
        if (t.contains("饮食") || t.contains("营养") || t.contains("减肥") || t.contains("水果") || t.contains("食谱") || t.contains("维生素")) return "饮食营养";
        if (t.contains("皮肤") || t.contains("痘痘") || t.contains("湿疹") || t.contains("过敏") || t.contains("防晒") || t.contains("痤疮")) return "皮肤问题";
        if (t.contains("头痛") || t.contains("头晕") || t.contains("偏头痛") || t.contains("眩晕")) return "头痛头晕";
        if (t.contains("高血压") || t.contains("低血压") || t.contains("心脏") || t.contains("心悸") || t.contains("血脂")) return "心血管";
        if (t.contains("眼睛") || t.contains("视力") || t.contains("近视") || t.contains("干眼") || t.contains("眼疲劳")) return "眼科";
        if (t.contains("牙痛") || t.contains("口腔") || t.contains("龋齿") || t.contains("牙龈")) return "口腔";
        if (t.contains("焦虑") || t.contains("抑郁") || t.contains("心理") || t.contains("压力") || t.contains("情绪")) return "心理健康";
        if (t.contains("月经") || t.contains("妇科") || t.contains("怀孕") || t.contains("乳腺")) return "妇科";
        if (t.contains("宝宝") || t.contains("小儿") || t.contains("婴儿") || t.contains("儿童")) return "儿科";
        return "综合咨询";
    }

    @Override
    public List<Map<String, Object>> getUserTrend(int days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd");

        List<Map<String, Object>> rows = userMapper.countByDateGroup(startDate.atStartOfDay());

        Map<String, Long> dateCountMap = new HashMap<>();
        for (Map<String, Object> row : rows) {
            String dt = String.valueOf(row.get("dt"));
            long cnt = ((Number) row.get("cnt")).longValue();
            dateCountMap.put(dt, cnt);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(fmt));
            item.put("count", dateCountMap.getOrDefault(date.toString(), 0L));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getConsultTrend(int days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd");

        List<Map<String, Object>> sessionRows = sessionMapper.countByDateGroup(startDate.atStartOfDay());
        List<Map<String, Object>> messageRows = messageMapper.countByDateGroup(startDate.atStartOfDay());

        Map<String, Long> sessionMap = new HashMap<>();
        for (Map<String, Object> row : sessionRows) {
            sessionMap.put(String.valueOf(row.get("dt")), ((Number) row.get("cnt")).longValue());
        }
        Map<String, Long> messageMap = new HashMap<>();
        for (Map<String, Object> row : messageRows) {
            messageMap.put(String.valueOf(row.get("dt")), ((Number) row.get("cnt")).longValue());
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(fmt));
            item.put("sessions", sessionMap.getOrDefault(date.toString(), 0L));
            item.put("messages", messageMap.getOrDefault(date.toString(), 0L));
            result.add(item);
        }
        return result;
    }

    @Override
    public Map<String, Object> getMemberStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userMapper.selectCount(null);

        List<UserMembership> memberships = userMembershipMapper.selectList(
                new LambdaQueryWrapper<UserMembership>()
                        .eq(UserMembership::getStatus, 1)
                        .eq(UserMembership::getIsDeleted, 0));

        Map<String, Set<Long>> levelUsers = memberships.stream()
                .collect(Collectors.groupingBy(
                        UserMembership::getLevelCode,
                        Collectors.mapping(UserMembership::getUserId, Collectors.toSet())));

        long l1 = levelUsers.getOrDefault("L1", Collections.emptySet()).size();
        long l2 = levelUsers.getOrDefault("L2", Collections.emptySet()).size();
        long l3 = levelUsers.getOrDefault("L3", Collections.emptySet()).size();
        long l0 = Math.max(0, totalUsers - l1 - l2 - l3);

        Set<Long> allMemberUserIds = levelUsers.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        stats.put("totalMembers", allMemberUserIds.size());
        stats.put("L0", l0);
        stats.put("L1", l1);
        stats.put("L2", l2);
        stats.put("L3", l3);

        return stats;
    }

    @Override
    public List<Map<String, Object>> getHotConsults(int limit) {
        List<HealthSearchCache> caches = healthSearchCacheMapper.selectList(
                new LambdaQueryWrapper<HealthSearchCache>()
                        .orderByDesc(HealthSearchCache::getHitCount)
                        .last("LIMIT " + limit));

        List<Map<String, Object>> result = new ArrayList<>();
        for (HealthSearchCache cache : caches) {
            Map<String, Object> item = new HashMap<>();
            item.put("keyword", cache.getKeyword());
            item.put("hitCount", cache.getHitCount());
            item.put("source", cache.getSource());
            result.add(item);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getRecentConsults(int limit) {
        List<Message> messages = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getRole, "user")
                        .orderByDesc(Message::getCreatedTime)
                        .last("LIMIT " + limit));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Message message : messages) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", message.getId());
            item.put("userId", message.getUserId());
            item.put("content", truncateContent(message.getContent(), 50));
            item.put("createdAt", message.getCreatedTime());
            result.add(item);
        }

        return result;
    }

    @Override
    public Map<String, Object> getAiPerformanceStats() {
        Map<String, Object> stats = new HashMap<>();

        Map<String, Object> row = messageMapper.getAiPerformanceStats();

        if (row == null || ((Number) row.get("totalAiCalls")).longValue() == 0) {
            stats.put("avgResponseTime", 0);
            stats.put("maxResponseTime", 0);
            stats.put("minResponseTime", 0);
            stats.put("totalAiCalls", 0);
            return stats;
        }

        stats.put("totalAiCalls", ((Number) row.get("totalAiCalls")).longValue());
        stats.put("avgResponseTime", ((Number) row.get("avgResponseTime")).longValue());
        stats.put("maxResponseTime", ((Number) row.get("maxResponseTime")).longValue());
        stats.put("minResponseTime", ((Number) row.get("minResponseTime")).longValue());
        return stats;
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
