package com.neusoft.health.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.modules.user.dto.UserSettingUpdateDTO;
import com.neusoft.health.modules.user.entity.UserSetting;
import com.neusoft.health.modules.user.mapper.UserSettingMapper;
import com.neusoft.health.modules.user.service.UserSettingService;
import com.neusoft.health.modules.user.vo.UserSettingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSettingServiceImpl extends ServiceImpl<UserSettingMapper, UserSetting> implements UserSettingService {

    @Override
    public UserSettingVO getSettings(Long userId) {
        return getByUserId(userId);
    }

    @Override
    public UserSettingVO updateSettings(Long userId, UserSettingUpdateDTO dto) {
        return updateSetting(userId, dto);
    }

    @Override
    public UserSettingVO getByUserId(Long userId) {
        UserSetting setting = getOne(new LambdaQueryWrapper<UserSetting>()
                .eq(UserSetting::getUserId, userId));
        if (setting == null) {
            return null;
        }
        UserSettingVO vo = new UserSettingVO();
        BeanUtils.copyProperties(setting, vo);
        return vo;
    }

    @Override
    public UserSettingVO updateSetting(Long userId, UserSettingUpdateDTO dto) {
        UserSetting setting = getOne(new LambdaQueryWrapper<UserSetting>()
                .eq(UserSetting::getUserId, userId));
        if (setting == null) {
            setting = new UserSetting();
            setting.setUserId(userId);
        }
        if (dto.getNotificationEnabled() != null) setting.setNotificationEnabled(dto.getNotificationEnabled());
        if (dto.getVoiceSpeed() != null) setting.setVoiceSpeed(dto.getVoiceSpeed());
        if (dto.getVoiceVolume() != null) setting.setVoiceVolume(dto.getVoiceVolume());
        if (dto.getVoiceTone() != null) setting.setVoiceTone(dto.getVoiceTone());
        if (dto.getAnonymousMode() != null) setting.setAnonymousMode(dto.getAnonymousMode());
        if (dto.getRecommendEnabled() != null) setting.setRecommendEnabled(dto.getRecommendEnabled());
        saveOrUpdate(setting);
        return getByUserId(userId);
    }
}