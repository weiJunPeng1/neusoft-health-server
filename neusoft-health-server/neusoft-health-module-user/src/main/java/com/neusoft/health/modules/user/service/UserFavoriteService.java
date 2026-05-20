package com.neusoft.health.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.user.entity.UserFavorite;
import com.neusoft.health.modules.user.vo.UserFavoriteVO;

import java.util.List;

public interface UserFavoriteService extends IService<UserFavorite> {
    boolean toggleFavorite(Long userId, Long messageId);

    boolean checkFavorite(Long userId, Long messageId);

    List<UserFavoriteVO> listFavorites(Long userId);


    void toggle(Long userId, Long messageId);

    boolean isFavorited(Long userId, Long messageId);

    List<UserFavoriteVO> listByUserId(Long userId);
}

