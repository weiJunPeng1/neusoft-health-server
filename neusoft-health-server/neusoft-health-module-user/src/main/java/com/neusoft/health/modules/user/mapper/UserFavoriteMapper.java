package com.neusoft.health.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.health.modules.user.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {

    @Select("SELECT count(*) FROM user_favorites WHERE user_id = #{userId} AND message_id = #{messageId}")
    int countByUserAndMessage(Long userId, Long messageId);

    @Select("SELECT id FROM user_favorites WHERE user_id = #{userId} AND message_id = #{messageId} LIMIT 1")
    Long findIdByUserAndMessage(Long userId, Long messageId);
}
