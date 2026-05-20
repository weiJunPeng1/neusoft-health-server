package com.neusoft.health.modules.consultation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.consultation.entity.EmergencyLog;
import com.neusoft.health.modules.consultation.vo.EmergencyLogVO;

import java.util.List;

public interface EmergencyService extends IService<EmergencyLog> {

    EmergencyLog createEmergencyLog(Long userId, Long messageId, String keyword);

    List<EmergencyLogVO> listEmergencyLogs(Long userId);

    void handleEmergency(Long id);
}
