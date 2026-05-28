package com.neusoft.health.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.user.dto.UserHealthProfileUpdateDTO;
import com.neusoft.health.modules.user.entity.UserHealthProfile;
import com.neusoft.health.modules.user.vo.UserHealthProfileVO;

public interface UserHealthProfileService extends IService<UserHealthProfile> {
    UserHealthProfileVO getProfile(Long userId);

    UserHealthProfileVO getByUserId(Long userId);

    UserHealthProfileVO updateProfile(Long userId, UserHealthProfileUpdateDTO dto);
}

