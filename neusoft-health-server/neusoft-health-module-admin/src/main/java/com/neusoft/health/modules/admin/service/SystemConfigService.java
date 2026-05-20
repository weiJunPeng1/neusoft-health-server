package com.neusoft.health.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.admin.dto.SystemConfigUpdateDTO;
import com.neusoft.health.modules.admin.entity.SystemConfig;
import com.neusoft.health.modules.admin.vo.SystemConfigVO;

import java.util.List;

/**
 * 系统配置服务
 * <p>
 * 提供系统配置的查询、更新、缓存管理等功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface SystemConfigService extends IService<SystemConfig> {

    /**
     * 获取所有配置列表
     *
     * @return 配置列表
     */
    List<SystemConfigVO> listAll();

    /**
     * 更新配置
     *
     * @param dto 配置更新DTO
     * @return 更新后的配置VO
     */
    SystemConfigVO updateConfig(SystemConfigUpdateDTO dto);

    /**
     * 根据配置键获取配置
     *
     * @param configKey 配置键
     * @return 配置VO
     */
    SystemConfigVO getByConfigKey(String configKey);

    /**
     * 获取配置值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getConfigValue(String configKey, String defaultValue);
}
