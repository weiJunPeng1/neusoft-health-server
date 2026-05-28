package com.neusoft.health.modules.consultation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.health.modules.consultation.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    @Select("SELECT DATE(created_time) AS dt, COUNT(*) AS cnt FROM messages WHERE is_deleted = 0 AND created_time >= #{startDate} GROUP BY dt ORDER BY dt")
    List<Map<String, Object>> countByDateGroup(LocalDateTime startDate);

    @Select("SELECT COUNT(*) AS totalAiCalls, IFNULL(AVG(api_call_duration), 0) AS avgResponseTime, IFNULL(MAX(api_call_duration), 0) AS maxResponseTime, IFNULL(MIN(api_call_duration), 0) AS minResponseTime FROM messages WHERE is_deleted = 0 AND role = 'assistant' AND api_call_duration IS NOT NULL")
    Map<String, Object> getAiPerformanceStats();
}
