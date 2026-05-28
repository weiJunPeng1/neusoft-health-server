package com.neusoft.health.modules.member.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.framework.util.SecurityUtil;
import com.neusoft.health.modules.member.dto.OrderCreateDTO;
import com.neusoft.health.modules.member.dto.RefundApplyDTO;
import com.neusoft.health.modules.member.service.AlipayService;
import com.neusoft.health.modules.member.service.PaymentService;
import com.neusoft.health.modules.member.service.RefundService;
import com.neusoft.health.modules.member.vo.PaymentOrderVO;
import com.neusoft.health.modules.member.vo.RefundRequestVO;
import com.neusoft.health.modules.member.vo.SubscriptionPlanVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "支付中心", description = "订阅方案、订单创建、支付宝回调接口")
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final RefundService refundService;
    private final AlipayService alipayService;

    @Operation(summary = "获取订阅方案列表")
    @GetMapping("/plans")
    public R<List<SubscriptionPlanVO>> listPlans() {
        return R.ok(paymentService.listPlans(SecurityUtil.requireCurrentUserId()));
    }

    @Operation(summary = "创建支付订单")
    @PostMapping("/order")
    public R<PaymentOrderVO> createOrder(@RequestBody OrderCreateDTO dto) {
        return R.ok(paymentService.createOrder(SecurityUtil.requireCurrentUserId(), dto));
    }

    @Operation(summary = "查询订单状态")
    @GetMapping("/order/{orderNo}")
    public R<PaymentOrderVO> queryOrder(@PathVariable String orderNo) {
        return R.ok(paymentService.queryOrder(orderNo));
    }

    @Operation(summary = "获取我的订单列表")
    @GetMapping("/orders")
    public R<List<PaymentOrderVO>> listOrders() {
        return R.ok(paymentService.listOrders(SecurityUtil.requireCurrentUserId()));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/order/{orderNo}/cancel")
    public R<Void> cancelOrder(@PathVariable String orderNo) {
        paymentService.cancelOrder(orderNo, SecurityUtil.requireCurrentUserId());
        return R.ok();
    }

    @Operation(summary = "生成支付二维码")
    @GetMapping("/qrcode/{orderNo}")
    public R<Map<String, String>> generateQrCode(@PathVariable String orderNo) {
        try {
            PaymentOrderVO order = paymentService.queryOrder(orderNo);
            String qrCode = alipayService.generateQrCode(
                    order.getOrderNo(),
                    order.getPlanName(),
                    order.getAmount().toString()
            );
            Map<String, String> result = new HashMap<>();
            result.put("qrCode", qrCode);
            result.put("orderNo", orderNo);
            return R.ok(result);
        } catch (Exception e) {
            log.error("生成支付二维码失败", e);
            return R.fail(500, "生成支付二维码失败：" + e.getMessage());
        }
    }

    @Operation(summary = "生成电脑网站支付URL")
    @GetMapping("/page-pay/{orderNo}")
    public R<Map<String, String>> createPagePayUrl(@PathVariable String orderNo, HttpServletRequest request) {
        try {
            PaymentOrderVO order = paymentService.queryOrder(orderNo);
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String baseUrl;
            if (("http".equals(scheme) && serverPort == 80) || ("https".equals(scheme) && serverPort == 443)) {
                baseUrl = scheme + "://" + serverName;
            } else {
                baseUrl = scheme + "://" + serverName + ":" + serverPort;
            }
            String pagePayUrl = baseUrl + "/api/v1/payment/alipay-pay-page/" + orderNo;
            Map<String, String> result = new HashMap<>();
            result.put("pagePayUrl", pagePayUrl);
            result.put("orderNo", orderNo);
            return R.ok(result);
        } catch (Exception e) {
            log.error("生成电脑网站支付URL失败", e);
            return R.fail(500, "生成电脑网站支付URL失败：" + e.getMessage());
        }
    }

    @Operation(summary = "支付宝支付页面（直接重定向）")
    @GetMapping("/alipay-pay-page/{orderNo}")
    public void alipayPayPage(@PathVariable String orderNo, HttpServletResponse response) throws IOException {
        try {
            PaymentOrderVO order = paymentService.queryOrder(orderNo);
            String alipayUrl = alipayService.createPagePayUrl(
                    order.getOrderNo(),
                    order.getPlanName(),
                    order.getAmount().toString()
            );
            
            response.sendRedirect(alipayUrl);
        } catch (Exception e) {
            log.error("生成支付宝支付页面失败", e);
            response.getWriter().write("<html><body><h1>支付页面生成失败</h1><p>" + e.getMessage() + "</p></body></html>");
        }
    }

    @Operation(summary = "主动查询支付宝订单状态")
    @GetMapping("/alipay/query/{orderNo}")
    public R<Map<String, Object>> queryAlipayOrder(@PathVariable String orderNo) {
        try {
            String result = alipayService.queryOrder(orderNo);
            Map<String, Object> response = new HashMap<>();
            response.put("orderNo", orderNo);
            response.put("alipayResult", result);
            return R.ok(response);
        } catch (Exception e) {
            log.error("查询支付宝订单失败", e);
            return R.fail(500, "查询支付宝订单失败：" + e.getMessage());
        }
    }

    @Operation(summary = "关闭支付宝订单")
    @PostMapping("/alipay/close/{orderNo}")
    public R<Map<String, String>> closeAlipayOrder(@PathVariable String orderNo) {
        try {
            String tradeNo = alipayService.closeOrder(orderNo);
            paymentService.cancelOrder(orderNo, SecurityUtil.requireCurrentUserId());
            Map<String, String> result = new HashMap<>();
            result.put("orderNo", orderNo);
            result.put("tradeNo", tradeNo);
            return R.ok(result);
        } catch (Exception e) {
            log.error("关闭支付宝订单失败", e);
            return R.fail(500, "关闭支付宝订单失败：" + e.getMessage());
        }
    }

    @Operation(summary = "发起支付宝退款")
    @PostMapping("/alipay/refund")
    public R<Map<String, String>> alipayRefund(@RequestBody Map<String, String> params) {
        try {
            String orderNo = params.get("orderNo");
            String refundRequestNo = params.get("refundRequestNo");
            String refundAmount = params.get("refundAmount");
            String reason = params.get("reason");

            String tradeNo = alipayService.refund(orderNo, refundRequestNo, refundAmount, reason);
            Map<String, String> result = new HashMap<>();
            result.put("orderNo", orderNo);
            result.put("tradeNo", tradeNo);
            result.put("refundRequestNo", refundRequestNo);
            return R.ok(result);
        } catch (Exception e) {
            log.error("支付宝退款失败", e);
            return R.fail(500, "支付宝退款失败：" + e.getMessage());
        }
    }

    @Operation(summary = "查询支付宝退款状态")
    @GetMapping("/alipay/refund/query")
    public R<Map<String, Object>> queryAlipayRefund(@RequestParam String orderNo, @RequestParam String refundRequestNo) {
        try {
            String result = alipayService.queryRefund(orderNo, refundRequestNo);
            Map<String, Object> response = new HashMap<>();
            response.put("orderNo", orderNo);
            response.put("refundRequestNo", refundRequestNo);
            response.put("alipayResult", result);
            return R.ok(response);
        } catch (Exception e) {
            log.error("查询支付宝退款失败", e);
            return R.fail(500, "查询支付宝退款失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取支付宝账单下载地址")
    @GetMapping("/alipay/bill/download-url")
    public R<Map<String, String>> getBillDownloadUrl(@RequestParam String billType, @RequestParam String billDate) {
        try {
            String downloadUrl = alipayService.queryBillDownloadUrl(billType, billDate);
            Map<String, String> result = new HashMap<>();
            result.put("billType", billType);
            result.put("billDate", billDate);
            result.put("downloadUrl", downloadUrl);
            return R.ok(result);
        } catch (Exception e) {
            log.error("获取支付宝账单下载地址失败", e);
            return R.fail(500, "获取支付宝账单下载地址失败：" + e.getMessage());
        }
    }

    @Operation(summary = "支付宝支付成功前端回调（无需登录）")
    @GetMapping("/return/alipay")
    public String alipayReturn(HttpServletRequest request) {
        try {
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                StringBuilder valueStr = new StringBuilder();
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) valueStr.append(",");
                    valueStr.append(values[i]);
                }
                params.put(name, valueStr.toString());
            }

            log.info("支付宝同步回调参数：{}", params);

            String orderNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            String totalAmount = params.get("total_amount");

            if (orderNo == null || orderNo.isEmpty()) {
                log.warn("支付宝同步回调缺少订单号参数");
                return buildResultPage(false, "支付参数异常", "", "", "");
            }

            // 同步回调仅用于展示，不依赖异步通知(notify)已可靠处理订单，直接展示成功页面即可
            // handleAlipayCallback 已做幂等处理，重复调用安全
            paymentService.handleAlipayCallback(orderNo, tradeNo);
            log.info("支付宝同步回调处理成功，订单号：{}", orderNo);
            return buildResultPage(true, "支付成功", orderNo, tradeNo, totalAmount);
        } catch (Exception e) {
            log.error("处理支付宝同步回调异常", e);
            return buildResultPage(false, "处理异常：" + e.getMessage(), "", "", "");
        }
    }

    private String buildResultPage(boolean success, String message, String orderNo, String tradeNo, String totalAmount) {
        String color = success ? "#52c41a" : "#ff4d4f";
        String icon = success ? "&#10004;" : "&#10008;";
        return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "<title>支付结果</title><style>"
                + "body{margin:0;padding:0;display:flex;justify-content:center;align-items:center;min-height:100vh;"
                + "font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;background:#f5f5f5;}"
                + ".card{background:#fff;border-radius:12px;padding:40px;text-align:center;box-shadow:0 2px 12px rgba(0,0,0,0.1);max-width:420px;width:90%;}"
                + ".icon{font-size:64px;margin-bottom:16px;}"
                + "h2{color:#333;margin:0 0 12px;}"
                + ".msg{color:#666;margin-bottom:24px;}"
                + ".info{text-align:left;background:#fafafa;border-radius:8px;padding:16px;margin-bottom:24px;font-size:14px;color:#666;}"
                + ".info p{margin:8px 0;}"
                + ".info span{color:#333;font-weight:500;}"
                + ".btn{display:inline-block;padding:10px 32px;border-radius:6px;background:" + color + ";color:#fff;text-decoration:none;font-size:16px;border:none;cursor:pointer;}"
                + "</style></head><body><div class=\"card\">"
                + "<div class=\"icon\" style=\"color:" + color + ";\">" + icon + "</div>"
                + "<h2>" + (success ? "支付成功" : "支付处理提示") + "</h2>"
                + "<p class=\"msg\">" + message + "</p>"
                + (orderNo != null && !orderNo.isEmpty()
                    ? "<div class=\"info\">"
                        + "<p>订单号：<span>" + orderNo + "</span></p>"
                        + (tradeNo != null && !tradeNo.isEmpty() ? "<p>交易号：<span>" + tradeNo + "</span></p>" : "")
                        + (totalAmount != null && !totalAmount.isEmpty() ? "<p>支付金额：<span>&#165;" + totalAmount + "</span></p>" : "")
                        + "</div>"
                    : "")
                + "<p id=\"countdown\" style=\"color:#999;font-size:13px;\">页面将在3秒后自动关闭...</p>"
                + "</div>"
                + "<script>"
                + "var count = 3;"
                + "var countdownEl = document.getElementById('countdown');"
                + "var timer = setInterval(function() {"
                + "  count--;"
                + "  if (count > 0) {"
                + "    countdownEl.textContent = '页面将在' + count + '秒后自动关闭...';"
                + "  } else {"
                + "    clearInterval(timer);"
                + "    try { window.opener = null; window.close(); } catch(e) { }"
                + "    try { if (window.history.length > 1) { window.history.go(-2); } } catch(e) { }"
                + "  }"
                + "}, 1000);"
                + "</script>"
                + "</body></html>";
    }

    @Operation(summary = "支付宝扫码支付回调（无需登录）")
    @PostMapping("/notify/alipay")
    public String alipayNotify(HttpServletRequest request) {
        try {
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }

            log.info("支付宝回调参数：{}", params);

            if (alipayService.verifyNotification(params)) {
                String orderNo = params.get("out_trade_no");
                String tradeNo = params.get("trade_no");
                String tradeStatus = params.get("trade_status");

                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    paymentService.handleAlipayCallback(orderNo, tradeNo);
                    log.info("支付宝支付成功，订单号：{}", orderNo);
                }
                return "success";
            } else {
                log.warn("支付宝签名验证失败");
                return "fail";
            }
        } catch (Exception e) {
            log.error("处理支付宝回调异常", e);
            return "fail";
        }
    }

    @Operation(summary = "支付宝退款到银行卡回调（无需登录）")
    @PostMapping("/notify/alipay/refund-depositback")
    public String alipayRefundDepositBackNotify(HttpServletRequest request) {
        try {
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }

            log.info("支付宝退款到银行卡回调参数：{}", params);

            if (alipayService.verifyRefundDepositBackNotification(params)) {
                String orderNo = params.get("out_trade_no");
                String refundRequestNo = params.get("out_request_no");
                String depositBackStatus = params.get("deposit_back_status");
                
                log.info("退款到银行卡完成，订单号：{}，退款请求号：{}，状态：{}", 
                        orderNo, refundRequestNo, depositBackStatus);
                
                refundService.handleDepositBackCallback(orderNo, refundRequestNo, depositBackStatus);
                return "success";
            } else {
                log.warn("支付宝退款到银行卡回调签名验证失败");
                return "fail";
            }
        } catch (Exception e) {
            log.error("处理支付宝退款到银行卡回调异常", e);
            return "fail";
        }
    }

    @Operation(summary = "申请退款")
    @PostMapping("/refund/apply")
    public R<Void> applyRefund(@RequestBody @Valid RefundApplyDTO dto) {
        refundService.applyRefund(SecurityUtil.requireCurrentUserId(), dto);
        return R.ok();
    }

    @Operation(summary = "获取我的退款记录")
    @GetMapping("/refund/my")
    public R<List<RefundRequestVO>> getMyRefunds() {
        return R.ok(refundService.getUserRefundList(SecurityUtil.requireCurrentUserId()));
    }
}