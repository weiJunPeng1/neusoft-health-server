package com.neusoft.health.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.user.dto.UserSettingUpdateDTO;
import com.neusoft.health.modules.user.entity.UserSetting;
import com.neusoft.health.modules.user.vo.UserSettingVO;

/**
 * 用户设置服务
 * <p>
 * 提供用户设置的查询、更新功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface UserSettingService extends IService<UserSetting> {

    /**
     * 获取用户设置
     *
     * @param userId 用户ID
     * @return 用户设置VO
     */
    UserSettingVO getSettings(Long userId);

    /**
     * 更新用户设置
     *
     * @param userId 用户ID
     * @param dto 设置更新DTO
     * @return 用户设置VO
     */
    UserSettingVO updateSettings(Long userId, UserSettingUpdateDTO dto);

    /**
     * 根据用户ID获取设置
     *
     * @param userId 用户ID
     * @return 用户设置VO
     */
    UserSettingVO getByUserId(Long userId);

    /**
     * 更新用户设置
     *
     * @param userId 用户ID
     * @param dto 设置更新DTO
     * @return 用户设置VO
     */
    UserSettingVO updateSetting(Long userId, UserSettingUpdateDTO dto);
}

