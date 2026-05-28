package com.neusoft.health.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.health.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户Mapper
 * <p>
 * 提供用户数据访问功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据手机号哈希查找用户
     *
     * @param phoneHash 手机号哈希
     * @return 用户对象，Optional包装
     */
    default Optional<User> findByPhoneHash(String phoneHash) {
        User user = selectOne(new LambdaQueryWrapper<User>().eq(User::getPhoneHash, phoneHash));
        return Optional.ofNullable(user);
    }

    @Select("SELECT DATE(created_time) AS dt, COUNT(*) AS cnt FROM users WHERE is_deleted = 0 AND created_time >= #{startDate} GROUP BY dt ORDER BY dt")
    List<Map<String, Object>> countByDateGroup(java.time.LocalDateTime startDate);
}
