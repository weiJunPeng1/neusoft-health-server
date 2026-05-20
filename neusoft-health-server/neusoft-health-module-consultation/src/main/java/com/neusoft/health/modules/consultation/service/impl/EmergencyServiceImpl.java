package com.neusoft.health.modules.consultation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.modules.consultation.entity.EmergencyLog;
import com.neusoft.health.modules.consultation.mapper.EmergencyLogMapper;
import com.neusoft.health.modules.consultation.service.EmergencyService;
import com.neusoft.health.modules.consultation.vo.EmergencyLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmergencyServiceImpl extends ServiceImpl<EmergencyLogMapper, EmergencyLog> implements EmergencyService {

    @Override
    public EmergencyLog createEmergencyLog(Long userId, Long messageId, String keyword) {
        EmergencyLog log = new EmergencyLog();
        log.setUserId(userId);
        log.setMessageId(messageId);
        log.setKeywordMatched(keyword);
        log.setHandled(0);
        save(log);
        return log;
    }

    @Override
    public List<EmergencyLogVO> listEmergencyLogs(Long userId) {
        List<EmergencyLog> logs = list(new LambdaQueryWrapper<EmergencyLog>()
                .eq(EmergencyLog::getUserId, userId)
                .orderByDesc(EmergencyLog::getCreatedTime));
        return logs.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void handleEmergency(Long id) {
        EmergencyLog log = getById(id);
        if (log != null) {
            log.setHandled(1);
            updateById(log);
        }
    }

    private EmergencyLogVO toVO(EmergencyLog log) {
        EmergencyLogVO vo = new EmergencyLogVO();
        BeanUtils.copyProperties(log, vo);
        return vo;
    }
}
