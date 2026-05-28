package com.neusoft.health.modules.consultation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.health.modules.consultation.entity.Session;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface SessionMapper extends BaseMapper<Session> {

    @Select("SELECT DATE(created_time) AS dt, COUNT(*) AS cnt FROM sessions WHERE is_deleted = 0 AND created_time >= #{startDate} GROUP BY dt ORDER BY dt")
    List<Map<String, Object>> countByDateGroup(LocalDateTime startDate);
}
