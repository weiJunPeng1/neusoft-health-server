package com.neusoft.health.modules.member.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.health.common.result.R;
import com.neusoft.health.framework.util.SecurityUtil;
import com.neusoft.health.modules.member.entity.PaymentOrder;
import com.neusoft.health.modules.member.entity.SubscriptionPlan;
import com.neusoft.health.modules.member.mapper.PaymentOrderMapper;
import com.neusoft.health.modules.member.mapper.SubscriptionPlanMapper;
import com.neusoft.health.modules.member.service.RefundService;
import com.neusoft.health.modules.member.vo.PaymentOrderVO;
import com.neusoft.health.modules.member.vo.RefundRequestVO;
import com.neusoft.health.modules.member.vo.SubscriptionPlanVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "支付管理后台", description = "订单管理、退款审核接口")
@RestController
@RequestMapping("/api/v1/admin/payment")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('R002', 'R003')")
public class AdminPaymentController {

    private final PaymentOrderMapper paymentOrderMapper;
    private final SubscriptionPlanMapper subscriptionPlanMapper;
    private final RefundService refundService;

    @Operation(summary = "获取支付订单列表")
    @GetMapping("/orders")
    public R<List<PaymentOrderVO>> getOrderList() {
        List<PaymentOrder> orders = paymentOrderMapper.selectList(
                new LambdaQueryWrapper<PaymentOrder>()
                        .orderByDesc(PaymentOrder::getCreatedTime)
        );
        return R.ok(orders.stream().map(this::toOrderVO).collect(Collectors.toList()));
    }

    @Operation(summary = "获取退款申请列表")
    @GetMapping("/refunds")
    public R<List<RefundRequestVO>> getRefundList() {
        return R.ok(refundService.getRefundList());
    }

    @Operation(summary = "审核通过退款申请")
    @PostMapping("/refund/{id}/approve")
    @PreAuthorize("hasRole('R002')")
    public R<Void> approveRefund(@PathVariable Long id, @RequestParam(required = false) String remark) {
        refundService.approveRefund(id, SecurityUtil.requireCurrentUserId(), remark);
        return R.ok();
    }

    @Operation(summary = "审核拒绝退款申请")
    @PostMapping("/refund/{id}/reject")
    @PreAuthorize("hasRole('R002')")
    public R<Void> rejectRefund(@PathVariable Long id, @RequestParam(required = false) String remark) {
        refundService.rejectRefund(id, SecurityUtil.requireCurrentUserId(), remark);
        return R.ok();
    }

    private PaymentOrderVO toOrderVO(PaymentOrder order) {
        PaymentOrderVO vo = new PaymentOrderVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setPlanId(order.getPlanId());
        vo.setAmount(order.getAmount());
        vo.setPayMethod(order.getPayMethod());
        vo.setPayStatus(order.getPayStatus());
        switch (order.getPayStatus()) {
            case 0: vo.setPayStatusDesc("待支付"); break;
            case 1: vo.setPayStatusDesc("已支付"); break;
            case 2: vo.setPayStatusDesc("已取消"); break;
            case 3: vo.setPayStatusDesc("退款中"); break;
            case 4: vo.setPayStatusDesc("已到账"); break;
            case 5: vo.setPayStatusDesc("到账失败"); break;
        }
        vo.setPaidTime(order.getPaidTime());
        vo.setExpireTime(order.getExpireTime());
        vo.setCreatedTime(order.getCreatedTime());
        
        SubscriptionPlan plan = subscriptionPlanMapper.selectById(order.getPlanId());
        if (plan != null) {
            vo.setPlanName(plan.getPlanName());
        }
        
        return vo;
    }
}
