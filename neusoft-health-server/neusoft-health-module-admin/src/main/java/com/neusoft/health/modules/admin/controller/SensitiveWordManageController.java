package com.neusoft.health.modules.admin.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.common.annotation.AdminOperation;
import com.neusoft.health.modules.admin.service.SensitiveWordManageService;
import com.neusoft.health.modules.consultation.entity.SensitiveWord;
import com.neusoft.health.modules.consultation.vo.SensitiveWordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "敏感词管理", description = "管理员敏感词管理接口，支持敏感词的增删改查操作")
@RestController
@RequestMapping("/api/v1/admin/sensitive-word")
@RequiredArgsConstructor
public class SensitiveWordManageController {

    private final SensitiveWordManageService sensitiveWordManageService;

    @Operation(summary = "获取敏感词列表", description = "获取所有敏感词条目列表")
    @GetMapping
    public R<List<SensitiveWordVO>> listAll() {
        return R.ok(sensitiveWordManageService.listAll());
    }

    @Operation(summary = "新增敏感词", description = "创建一条新的敏感词记录")
    @AdminOperation(module = "敏感词管理", operation = "新增敏感词", targetType = "SensitiveWord", targetIdParam = "#word.id")
    @PostMapping("/create")
    public R<SensitiveWord> create(@RequestBody SensitiveWord word) {
        return R.ok(sensitiveWordManageService.create(word));
    }

    @Operation(summary = "编辑敏感词", description = "修改已有的敏感词内容")
    @AdminOperation(module = "敏感词管理", operation = "编辑敏感词", targetType = "SensitiveWord", targetIdParam = "#word.id")
    @PutMapping
    public R<SensitiveWord> update(@RequestBody SensitiveWord word) {
        return R.ok(sensitiveWordManageService.updateWord(word));
    }

    @Operation(summary = "删除敏感词", description = "根据ID删除指定的敏感词")
    @AdminOperation(module = "敏感词管理", operation = "删除敏感词", targetType = "SensitiveWord", targetIdParam = "#id")
    @DeleteMapping("/{id}")
    public R<Void> delete(
            @Parameter(description = "敏感词ID") @PathVariable Long id) {
        sensitiveWordManageService.deleteWord(id);
        return R.ok();
    }
}
