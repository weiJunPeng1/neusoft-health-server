package com.neusoft.health.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.modules.user.dto.UserHealthProfileUpdateDTO;
import com.neusoft.health.modules.user.entity.UserHealthProfile;
import com.neusoft.health.modules.user.mapper.UserHealthProfileMapper;
import com.neusoft.health.modules.user.service.UserHealthProfileService;
import com.neusoft.health.modules.user.vo.UserHealthProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserHealthProfileServiceImpl extends ServiceImpl<UserHealthProfileMapper, UserHealthProfile> implements UserHealthProfileService {

    @Override
    public UserHealthProfileVO getProfile(Long userId) {
        return getByUserId(userId);
    }

    @Override
    public UserHealthProfileVO getByUserId(Long userId) {
        UserHealthProfile profile = getOne(new LambdaQueryWrapper<UserHealthProfile>()
                .eq(UserHealthProfile::getUserId, userId));
        if (profile == null) {
            return null;
        }
        UserHealthProfileVO vo = new UserHealthProfileVO();
        BeanUtils.copyProperties(profile, vo);
        return vo;
    }

    @Override
    public UserHealthProfileVO updateProfile(Long userId, UserHealthProfileUpdateDTO dto) {
        UserHealthProfile profile = getOne(new LambdaQueryWrapper<UserHealthProfile>()
                .eq(UserHealthProfile::getUserId, userId));
        if (profile == null) {
            profile = new UserHealthProfile();
            profile.setUserId(userId);
        }
        if (dto.getHeight() != null) profile.setHeight(dto.getHeight());
        if (dto.getWeight() != null) profile.setWeight(dto.getWeight());
        if (dto.getBloodType() != null) profile.setBloodType(dto.getBloodType());
        if (dto.getAllergies() != null) profile.setAllergies(dto.getAllergies());
        if (dto.getMedicalHistory() != null) profile.setMedicalHistory(dto.getMedicalHistory());
        if (dto.getMedicationHistory() != null) profile.setMedicationHistory(dto.getMedicationHistory());
        if (dto.getFamilyHistory() != null) profile.setFamilyHistory(dto.getFamilyHistory());
        if (dto.getAutoSync() != null) profile.setAutoSync(dto.getAutoSync());
        saveOrUpdate(profile);
        return getByUserId(userId);
    }
}