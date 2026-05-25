package com.neusoft.health.modules.consultation.service;

import com.neusoft.health.modules.consultation.vo.HealthSearchVO;

public interface HealthSearchService {

    HealthSearchVO search(String keyword);
}
