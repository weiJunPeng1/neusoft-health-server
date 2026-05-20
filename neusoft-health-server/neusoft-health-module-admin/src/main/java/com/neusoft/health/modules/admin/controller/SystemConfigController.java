package com.neusoft.health.modules.admin.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.modules.admin.dto.SystemConfigUpdateDTO;
import com.neusoft.health.modules.admin.service.SystemConfigService;
import com.neusoft.health.modules.admin.vo.SystemConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置控制器
 * <p>
 * 提供系统配置的查询、更新等功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Tag(name = "系统配置", description = "系统配置管理接口，支持系统参数的查询与修改")
@RestController
@RequestMapping("/api/v1/admin/config")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    /**
     * 获取配置列表
     *
     * @return 配置列表
     */
    @Operation(summary = "获取配置列表", description = "获取所有系统配置项列表，包含各配置的键、值和说明")
    @GetMapping
    public R<List<SystemConfigVO>> listConfigs() {
        return R.ok(systemConfigService.listAll());
    }

    /**
     * 获取配置详情
     *
     * @param configKey 配置键
     * @return 配置详情
     */
    @Operation(summary = "获取配置详情", description = "根据配置键获取指定系统配置项的详细信息")
    @GetMapping("/{configKey}")
    public R<SystemConfigVO> getConfig(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        return R.ok(systemConfigService.getByConfigKey(configKey));
    }

    /**
     * 更新配置
     *
     * @param dto 配置更新DTO
     * @return 更新后的配置
     */
    @Operation(summary = "更新配置", description = "修改系统配置项的值，支持批量更新")
    @PutMapping
    public R<SystemConfigVO> updateConfig(@RequestBody SystemConfigUpdateDTO dto) {
        return R.ok(systemConfigService.updateConfig(dto));
    }
}
