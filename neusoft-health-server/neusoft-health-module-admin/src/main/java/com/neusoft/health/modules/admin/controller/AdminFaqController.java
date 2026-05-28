package com.neusoft.health.modules.admin.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.common.annotation.AdminOperation;
import com.neusoft.health.modules.admin.service.AdminFaqService;
import com.neusoft.health.modules.consultation.entity.Faq;
import com.neusoft.health.modules.consultation.entity.FaqCategory;
import com.neusoft.health.modules.consultation.vo.FaqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "FAQ管理", description = "管理员FAQ管理接口，支持FAQ条目的增删改查以及分类管理")
@RestController
@RequestMapping("/api/v1/admin/faq")
@RequiredArgsConstructor
public class AdminFaqController {

    private final AdminFaqService adminFaqService;

    @Operation(summary = "获取FAQ列表", description = "按分类ID筛选获取所有FAQ条目列表，不传分类ID则获取全部")
    @GetMapping("/list")
    public R<List<FaqVO>> listFaqs(
            @Parameter(description = "分类ID，可选") @RequestParam(required = false) Long categoryId) {
        return R.ok(adminFaqService.listAllFaqs(categoryId));
    }

    @Operation(summary = "新增FAQ", description = "创建一条新的FAQ条目")
    @AdminOperation(module = "FAQ管理", operation = "新增FAQ", targetType = "Faq", targetIdParam = "#faq.id")
    @PostMapping("/create")
    public R<Faq> createFaq(@RequestBody Faq faq) {
        return R.ok(adminFaqService.createFaq(faq));
    }

    @Operation(summary = "编辑FAQ", description = "修改已有的FAQ条目内容")
    @AdminOperation(module = "FAQ管理", operation = "编辑FAQ", targetType = "Faq", targetIdParam = "#faq.id")
    @PutMapping
    public R<Faq> updateFaq(@RequestBody Faq faq) {
        return R.ok(adminFaqService.updateFaq(faq));
    }

    @Operation(summary = "删除FAQ", description = "根据ID删除指定的FAQ条目")
    @AdminOperation(module = "FAQ管理", operation = "删除FAQ", targetType = "Faq", targetIdParam = "#id")
    @DeleteMapping("/{id}")
    public R<Void> deleteFaq(
            @Parameter(description = "FAQ条目ID") @PathVariable Long id) {
        adminFaqService.deleteFaq(id);
        return R.ok();
    }

    @Operation(summary = "获取分类列表", description = "获取所有FAQ分类列表")
    @GetMapping("/categories")
    public R<List<FaqCategory>> listCategories() {
        return R.ok(adminFaqService.listAllCategories());
    }

    @Operation(summary = "新增分类", description = "创建新的FAQ分类")
    @AdminOperation(module = "FAQ管理", operation = "新增分类", targetType = "FaqCategory", targetIdParam = "#category.id")
    @PostMapping("/category")
    public R<FaqCategory> createCategory(@RequestBody FaqCategory category) {
        return R.ok(adminFaqService.createCategory(category));
    }

    @Operation(summary = "编辑分类", description = "修改已有的FAQ分类信息")
    @AdminOperation(module = "FAQ管理", operation = "编辑分类", targetType = "FaqCategory", targetIdParam = "#category.id")
    @PutMapping("/category")
    public R<FaqCategory> updateCategory(@RequestBody FaqCategory category) {
        return R.ok(adminFaqService.updateCategory(category));
    }

    @Operation(summary = "删除分类", description = "根据ID删除指定的FAQ分类")
    @AdminOperation(module = "FAQ管理", operation = "删除分类", targetType = "FaqCategory", targetIdParam = "#id")
    @DeleteMapping("/category/{id}")
    public R<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        adminFaqService.deleteCategory(id);
        return R.ok();
    }
}
