package com.neusoft.health.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.modules.admin.dto.SystemConfigUpdateDTO;
import com.neusoft.health.modules.admin.entity.SystemConfig;
import com.neusoft.health.modules.admin.mapper.SystemConfigMapper;
import com.neusoft.health.modules.admin.service.SystemConfigService;
import com.neusoft.health.modules.admin.vo.SystemConfigVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    @Override
    public List<SystemConfigVO> listAll() {
        List<SystemConfig> configs = list();
        return configs.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public SystemConfigVO updateConfig(SystemConfigUpdateDTO dto) {
        SystemConfig config = getById(dto.getId());
        if (config == null) {
            config = new SystemConfig();
            config.setConfigKey(dto.getConfigKey());
        }
        if (dto.getConfigValue() != null) config.setConfigValue(dto.getConfigValue());
        if (dto.getDescription() != null) config.setDescription(dto.getDescription());
        saveOrUpdate(config);
        return toVO(config);
    }

    @Override
    public SystemConfigVO getByConfigKey(String configKey) {
        SystemConfig config = getOne(new LambdaQueryWrapper<SystemConfig>()
                .eq(SystemConfig::getConfigKey, configKey));
        return config != null ? toVO(config) : null;
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        SystemConfig config = getOne(new LambdaQueryWrapper<SystemConfig>()
                .eq(SystemConfig::getConfigKey, configKey));
        return config != null ? config.getConfigValue() : defaultValue;
    }

    private SystemConfigVO toVO(SystemConfig config) {
        SystemConfigVO vo = new SystemConfigVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }
}
