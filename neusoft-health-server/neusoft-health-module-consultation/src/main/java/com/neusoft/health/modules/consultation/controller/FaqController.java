package com.neusoft.health.modules.consultation.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.modules.consultation.service.FaqCategoryService;
import com.neusoft.health.modules.consultation.vo.FaqCategoryTreeVO;
import com.neusoft.health.modules.consultation.vo.FaqCategoryVO;
import com.neusoft.health.modules.consultation.vo.FaqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "常见问题(FAQ)", description = "健康常见问题分类与内容查询接口，无需登录即可访问")
@RestController
@RequestMapping("/api/v1/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqCategoryService faqCategoryService;

    @Operation(summary = "获取FAQ分类列表", description = "获取所有FAQ分类的扁平列表，包含每个分类下的FAQ数量")
    @GetMapping("/categories")
    public R<List<FaqCategoryVO>> listCategories() {
        return R.ok(faqCategoryService.listCategories());
    }

    @Operation(summary = "获取FAQ分类树", description = "获取FAQ分类及其下的FAQ条目组成的树形结构数据")
    @GetMapping("/categories/tree")
    public R<List<FaqCategoryTreeVO>> listCategoryTree() {
        return R.ok(faqCategoryService.listCategoryTree());
    }

    @Operation(summary = "获取分类下的FAQ列表", description = "根据分类ID获取该分类下的所有常见问题列表")
    @GetMapping("/category/{id}")
    public R<List<FaqVO>> listByCategory(
            @Parameter(description = "FAQ分类ID") @PathVariable Long id) {
        return R.ok(faqCategoryService.listFaqsByCategory(id));
    }
}
