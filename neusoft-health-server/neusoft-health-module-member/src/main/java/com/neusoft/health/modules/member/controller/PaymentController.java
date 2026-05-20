package com.neusoft.health.modules.member.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.framework.util.SecurityUtil;
import com.neusoft.health.modules.member.dto.OrderCreateDTO;
import com.neusoft.health.modules.member.dto.RefundApplyDTO;
import com.neusoft.health.modules.member.service.PaymentService;
import com.neusoft.health.modules.member.service.RefundService;
import com.neusoft.health.modules.member.vo.PaymentOrderVO;
import com.neusoft.health.modules.member.vo.RefundRequestVO;
import com.neusoft.health.modules.member.vo.SubscriptionPlanVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "支付中心", description = "订阅方案、订单创建、支付宝回调接口")
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final RefundService refundService;

    @Operation(summary = "获取订阅方案列表")
    @GetMapping("/plans")
    public R<List<SubscriptionPlanVO>> listPlans() {
        return R.ok(paymentService.listPlans(SecurityUtil.requireCurrentUserId()));
    }

    @Operation(summary = "创建支付订单")
    @PostMapping("/order")
    public R<PaymentOrderVO> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        return R.ok(paymentService.createOrder(SecurityUtil.requireCurrentUserId(), dto));
    }

    @Operation(summary = "查询订单状态")
    @GetMapping("/order/{orderNo}")
    public R<PaymentOrderVO> queryOrder(@PathVariable String orderNo) {
        return R.ok(paymentService.queryOrder(orderNo));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/order/{orderNo}/cancel")
    public R<Void> cancelOrder(@PathVariable String orderNo) {
        paymentService.cancelOrder(orderNo, SecurityUtil.requireCurrentUserId());
        return R.ok();
    }

    @Operation(summary = "支付宝支付回调（无需登录）")
    @PostMapping("/callback/alipay")
    public R<String> alipayCallback(@RequestBody Map<String, String> params) {
        String orderNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String status = params.get("trade_status");
        if ("TRADE_SUCCESS".equals(status) || "TRADE_FINISHED".equals(status)) {
            paymentService.handleAlipayCallback(orderNo, tradeNo);
        }
        return R.ok("success");
    }

    @Operation(summary = "申请退款")
    @PostMapping("/refund/apply")
    public R<Void> applyRefund(@Valid @RequestBody RefundApplyDTO dto) {
        refundService.applyRefund(SecurityUtil.requireCurrentUserId(), dto);
        return R.ok();
    }

    @Operation(summary = "获取我的退款记录")
    @GetMapping("/refund/my")
    public R<List<RefundRequestVO>> getMyRefunds() {
        return R.ok(refundService.getUserRefundList(SecurityUtil.requireCurrentUserId()));
    }
}