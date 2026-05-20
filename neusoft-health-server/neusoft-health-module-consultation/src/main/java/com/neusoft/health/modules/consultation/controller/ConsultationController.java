package com.neusoft.health.modules.consultation.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.framework.util.SecurityUtil;
import com.neusoft.health.modules.consultation.dto.MessageSendDTO;
import com.neusoft.health.modules.consultation.dto.SessionCreateDTO;
import com.neusoft.health.modules.consultation.service.MessageService;
import com.neusoft.health.modules.consultation.service.SessionService;
import com.neusoft.health.modules.consultation.vo.MessageVO;
import com.neusoft.health.modules.consultation.vo.SessionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "健康咨询", description = "AI健康咨询会话管理、消息发送与历史查询接口")
@RestController
@RequestMapping("/api/v1/consultation")
@RequiredArgsConstructor
public class ConsultationController {

    private final SessionService sessionService;
    private final MessageService messageService;

    @Operation(summary = "创建咨询会话", description = "创建一个新的AI健康咨询会话，返回会话详情")
    @PostMapping("/session")
    public R<SessionVO> createSession(@Valid @RequestBody SessionCreateDTO dto) {
        return R.ok(sessionService.createSession(SecurityUtil.requireCurrentUserId(), dto));
    }

    @Operation(summary = "获取会话列表", description = "获取当前用户的所有咨询会话列表，按最后消息时间倒序排列")
    @GetMapping("/sessions")
    public R<List<SessionVO>> listSessions() {
        return R.ok(sessionService.listSessions(SecurityUtil.requireCurrentUserId()));
    }

    @Operation(summary = "删除会话", description = "删除指定的咨询会话及其所有消息记录")
    @DeleteMapping("/session/{id}")
    public R<Void> deleteSession(
            @Parameter(description = "会话ID") @PathVariable Long id) {
        sessionService.deleteSession(SecurityUtil.requireCurrentUserId(), id);
        return R.ok();
    }

    @Operation(summary = "发送咨询消息", description = "向AI发送健康咨询消息并获取回复（单用户每分钟限流60次）")
    @PostMapping("/message")
    public R<MessageVO> sendMessage(@Valid @RequestBody MessageSendDTO dto) {
        return R.ok(messageService.sendMessage(SecurityUtil.requireCurrentUserId(), dto));
    }

    @Operation(summary = "获取会话消息列表", description = "获取指定会话的所有咨询消息记录，按创建时间正序排列")
    @GetMapping("/session/{id}/messages")
    public R<List<MessageVO>> listMessages(
            @Parameter(description = "会话ID") @PathVariable Long id) {
        return R.ok(messageService.listMessages(SecurityUtil.requireCurrentUserId(), id));
    }

    @Operation(summary = "获取紧急情况日志", description = "获取当前用户触发的所有急救关键词告警日志列表")
    @GetMapping("/emergency/logs")
    public R<List<com.neusoft.health.modules.consultation.vo.EmergencyLogVO>> listEmergencyLogs() {
        return R.ok(messageService.listEmergencyLogs(SecurityUtil.requireCurrentUserId()));
    }

    @Operation(summary = "处理紧急情况", description = "管理员标记处理指定的紧急告警记录")
    @PostMapping("/emergency/{id}/handle")
    public R<Void> handleEmergency(
            @Parameter(description = "紧急日志ID") @PathVariable Long id) {
        messageService.handleEmergency(id);
        return R.ok();
    }
}
