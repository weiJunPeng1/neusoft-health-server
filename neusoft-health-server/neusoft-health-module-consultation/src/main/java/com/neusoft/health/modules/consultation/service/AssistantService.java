package com.neusoft.health.modules.consultation.service;

import com.neusoft.health.modules.consultation.vo.AssistantResponseVO;

public interface AssistantService {

    AssistantResponseVO query(String feature, String healthProfile);
}