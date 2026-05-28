package com.neusoft.health.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.modules.user.entity.UserFavorite;
import com.neusoft.health.modules.user.mapper.UserFavoriteMapper;
import com.neusoft.health.modules.user.service.UserFavoriteService;
import com.neusoft.health.modules.user.vo.UserFavoriteVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {

    @Override
    public boolean toggleFavorite(Long userId, Long messageId) {
        toggle(userId, messageId);
        return isFavorited(userId, messageId);
    }

    @Override
    public boolean checkFavorite(Long userId, Long messageId) {
        return isFavorited(userId, messageId);
    }

    @Override
    public List<UserFavoriteVO> listFavorites(Long userId) {
        return listByUserId(userId);
    }

    @Override
    public void toggle(Long userId, Long messageId) {
        Long existingId = baseMapper.findIdByUserAndMessage(userId, messageId);
        if (existingId != null) {
            removeById(existingId);
        } else {
            UserFavorite favorite = new UserFavorite();
            favorite.setUserId(userId);
            favorite.setMessageId(messageId);
            save(favorite);
        }
    }

    @Override
    public boolean isFavorited(Long userId, Long messageId) {
        return count(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getMessageId, messageId)) > 0;
    }

    @Override
    public List<UserFavoriteVO> listByUserId(Long userId) {
        List<UserFavorite> favorites = list(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .orderByDesc(UserFavorite::getCreatedTime));
        return favorites.stream().map(fav -> {
            UserFavoriteVO vo = new UserFavoriteVO();
            BeanUtils.copyProperties(fav, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}