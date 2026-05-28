package com.neusoft.health.modules.admin.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.modules.admin.service.OperationLogService;
import com.neusoft.health.modules.admin.vo.OperationLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志控制器
 * <p>
 * 提供操作日志的查询功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Tag(name = "操作日志", description = "管理员操作日志查询接口，支持按用户和行为筛选")
@RestController
@RequestMapping("/api/v1/admin/logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    /**
     * 获取操作日志列表
     *
     * @param userId 用户ID
     * @param action 操作类型
     * @param page 页码
     * @param size 每页大小
     * @return 日志列表
     */
    @Operation(summary = "获取操作日志列表", description = "分页查询操作日志，可按用户ID和操作类型筛选")
    @GetMapping("/list")
    public R<List<OperationLogVO>> listLogs(
            @Parameter(description = "用户ID，可选") @RequestParam(required = false) Long userId,
            @Parameter(description = "操作类型，可选") @RequestParam(required = false) String action,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {
        return R.ok(operationLogService.listLogs(userId, action, page, size));
    }
}
