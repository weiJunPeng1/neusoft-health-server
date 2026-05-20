package com.neusoft.health.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.modules.system.dto.UserLoginLogQueryDTO;
import com.neusoft.health.modules.system.entity.UserLoginLog;
import com.neusoft.health.modules.system.mapper.UserLoginLogMapper;
import com.neusoft.health.modules.system.service.UserLoginLogService;
import com.neusoft.health.modules.system.vo.UserLoginLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLoginLogServiceImpl extends ServiceImpl<UserLoginLogMapper, UserLoginLog> implements UserLoginLogService {

    @Override
    public List<UserLoginLogVO> getUserLoginLogs(Long userId, UserLoginLogQueryDTO dto) {
        LambdaQueryWrapper<UserLoginLog> wrapper = new LambdaQueryWrapper<UserLoginLog>()
                .eq(UserLoginLog::getUserId, userId)
                .orderByDesc(UserLoginLog::getCreatedTime);

        if (dto != null && dto.getLoginResult() != null) {
            wrapper.eq(UserLoginLog::getLoginResult, dto.getLoginResult());
        }

        // Apply limit via page query DTO
        if (dto != null && dto.getPageSize() > 0) {
            wrapper.last("LIMIT " + dto.getPageSize());
        }

        List<UserLoginLog> logs = list(wrapper);

        return logs.stream().map(log -> {
            UserLoginLogVO vo = new UserLoginLogVO();
            BeanUtils.copyProperties(log, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
