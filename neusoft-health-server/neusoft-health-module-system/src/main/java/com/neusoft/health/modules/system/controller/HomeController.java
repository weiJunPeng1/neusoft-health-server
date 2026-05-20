package com.neusoft.health.modules.system.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.modules.system.service.HomeService;
import com.neusoft.health.modules.system.vo.HomeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 首页控制器
 * <p>
 * 提供应用首页数据接口，包含健康资讯、快捷入口等聚合信息
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Tag(name = "首页", description = "应用首页数据接口，提供健康资讯、快捷入口等聚合信息")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    /**
     * 获取首页数据
     *
     * @return 首页数据
     */
    @Operation(summary = "获取首页数据", description = "获取首页展示的健康资讯、热门问答、快捷功能入口等聚合数据，无需登录")
    @GetMapping("/home")
    public R<HomeVO> home() {
        return R.ok(homeService.getHomeData());
    }
}
