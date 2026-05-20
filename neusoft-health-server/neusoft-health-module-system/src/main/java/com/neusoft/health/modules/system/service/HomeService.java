package com.neusoft.health.modules.system.service;

import com.neusoft.health.modules.system.vo.HomeVO;

/**
 * 首页服务
 * <p>
 * 提供首页数据聚合功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface HomeService {

    /**
     * 获取首页数据
     *
     * @return 首页数据，包含健康资讯、FAQ等
     */
    HomeVO getHomeData();
}
