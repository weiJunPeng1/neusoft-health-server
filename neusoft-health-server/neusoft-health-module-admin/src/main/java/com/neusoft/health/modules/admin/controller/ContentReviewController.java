package com.neusoft.health.modules.admin.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.framework.util.SecurityUtil;
import com.neusoft.health.common.annotation.AdminOperation;
import com.neusoft.health.modules.admin.service.ContentReviewService;
import com.neusoft.health.modules.consultation.dto.MessageReviewDTO;
import com.neusoft.health.modules.consultation.vo.MessageDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "内容审核", description = "管理员内容审核接口，支持待审核消息查询和审核操作")
@RestController
@RequestMapping("/api/v1/admin/review")
@RequiredArgsConstructor
public class ContentReviewController {

    private final ContentReviewService contentReviewService;

    @Operation(summary = "获取待审核列表", description = "分页获取所有待审核的咨询消息列表")
    @GetMapping("/pending")
    public R<List<MessageDetailVO>> listPending(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {
        return R.ok(contentReviewService.listPendingReview(page, size));
    }

    @Operation(summary = "审核消息", description = "对指定的咨询消息进行审核通过或驳回操作")
    @AdminOperation(module = "内容审核", operation = "审核消息", targetType = "Message", targetIdParam = "#dto.messageId")
    @PostMapping("/message")
    public R<Void> reviewMessage(@Valid @RequestBody MessageReviewDTO dto) {
        Long reviewerId = SecurityUtil.getCurrentUserId();
        contentReviewService.reviewMessage(dto, reviewerId);
        return R.ok();
    }
}
