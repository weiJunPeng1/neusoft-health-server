package com.neusoft.health.modules.member.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.health.modules.member.entity.ConsultDailyQuota;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ConsultDailyQuotaMapper extends BaseMapper<ConsultDailyQuota> {

    @Select("SELECT consult_count FROM consult_daily_quota WHERE user_id = #{userId} AND consult_date = #{consultDate}")
    Integer getTodayCount(@Param("userId") Long userId, @Param("consultDate") java.time.LocalDate consultDate);

    @Update("UPDATE consult_daily_quota SET consult_count = consult_count + 1, updated_time = NOW() WHERE user_id = #{userId} AND consult_date = #{consultDate}")
    int incrementCount(@Param("userId") Long userId, @Param("consultDate") java.time.LocalDate consultDate);
}
