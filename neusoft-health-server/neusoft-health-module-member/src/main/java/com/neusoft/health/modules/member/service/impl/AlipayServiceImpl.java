package com.neusoft.health.modules.member.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neusoft.health.modules.member.config.AlipayConfig;
import com.neusoft.health.modules.member.service.AlipayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlipayServiceImpl implements AlipayService {

    private final AlipayConfig alipayConfig;
    private final ObjectMapper objectMapper;

    @Override
    public String generateQrCode(String orderNo, String subject, String amount) {
        validateAlipayConfig();
        
        try {
            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            request.setNotifyUrl(alipayConfig.getNotifyUrl());

            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", orderNo);
            bizContent.put("total_amount", new BigDecimal(amount));
            bizContent.put("subject", subject);
            bizContent.put("timeout_express", "15m");
            bizContent.put("product_code", "FACE_TO_FACE_PAYMENT");

            request.setBizContent(objectMapper.writeValueAsString(bizContent));

            AlipayTradePrecreateResponse response = createAlipayClient().execute(request);

            if (response.isSuccess()) {
                log.info("支付宝预下单成功，订单号：{}，二维码：{}", orderNo, response.getQrCode());
                return response.getQrCode();
            } else {
                log.error("支付宝预下单失败，订单号：{}，错误码：{}，错误信息：{}，详细错误：{}",
                        orderNo, response.getCode(), response.getMsg(), response.getSubMsg());
                throw new RuntimeException("支付宝预下单失败[" + response.getCode() + "]：" + response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            log.error("支付宝API调用异常，订单号：{}，错误：{}", orderNo, e.getErrMsg(), e);
            throw new RuntimeException("支付宝预下单异常：" + e.getErrMsg());
        } catch (Exception e) {
            log.error("JSON序列化异常", e);
            throw new RuntimeException("订单数据序列化失败");
        }
    }

    @Override
    public String createPagePayUrl(String orderNo, String subject, String amount) {
        validateAlipayConfig();
        
        try {
            AlipayClient alipayClient = createAlipayClient();
            
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
            request.setReturnUrl(alipayConfig.getReturnUrl());
            
            model.setOutTradeNo(orderNo);
            model.setTotalAmount(amount);
            model.setSubject(subject);
            model.setProductCode("FAST_INSTANT_TRADE_PAY");
            
            model.setQrPayMode("1");
            model.setQrcodeWidth(100L);
            
            String timeExpire = LocalDateTime.now().plusMinutes(15).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            model.setTimeExpire(timeExpire);
            
            model.setIntegrationType("PCWEB");
            
            request.setBizModel(model);
            
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "GET");

            if (response.isSuccess()) {
                log.info("支付宝电脑网站支付URL生成成功，订单号：{}", orderNo);
                return response.getBody();
            } else {
                log.error("支付宝电脑网站支付URL生成失败，订单号：{}，错误码：{}，错误信息：{}，详细错误：{}",
                        orderNo, response.getCode(), response.getMsg(), response.getSubMsg());
                throw new RuntimeException("支付宝电脑网站支付URL生成失败[" + response.getCode() + "]：" + response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            log.error("支付宝电脑网站支付API调用异常，订单号：{}，错误：{}", orderNo, e.getErrMsg(), e);
            throw new RuntimeException("支付宝电脑网站支付异常：" + e.getErrMsg());
        }
    }

    @Override
    public String queryOrder(String orderNo) {
        validateAlipayConfig();
        
        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", orderNo);

            request.setBizContent(objectMapper.writeValueAsString(bizContent));

            AlipayTradeQueryResponse response = createAlipayClient().execute(request);

            if (response.isSuccess()) {
                log.info("支付宝订单查询成功，订单号：{}，交易状态：{}", orderNo, response.getTradeStatus());
                return objectMapper.writeValueAsString(response);
            } else {
                log.error("支付宝订单查询失败，订单号：{}，错误码：{}，错误信息：{}",
                        orderNo, response.getCode(), response.getMsg());
                throw new RuntimeException("支付宝订单查询失败[" + response.getCode() + "]：" + response.getMsg());
            }
        } catch (AlipayApiException e) {
            log.error("支付宝订单查询API调用异常，订单号：{}，错误：{}", orderNo, e.getErrMsg(), e);
            throw new RuntimeException("支付宝订单查询异常：" + e.getErrMsg());
        } catch (Exception e) {
            log.error("JSON序列化异常", e);
            throw new RuntimeException("订单数据序列化失败");
        }
    }

    @Override
    public String closeOrder(String orderNo) {
        validateAlipayConfig();
        
        try {
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", orderNo);

            request.setBizContent(objectMapper.writeValueAsString(bizContent));

            AlipayTradeCloseResponse response = createAlipayClient().execute(request);

            if (response.isSuccess()) {
                log.info("支付宝订单关闭成功，订单号：{}", orderNo);
                return response.getTradeNo();
            } else {
                log.error("支付宝订单关闭失败，订单号：{}，错误码：{}，错误信息：{}",
                        orderNo, response.getCode(), response.getMsg());
                throw new RuntimeException("支付宝订单关闭失败[" + response.getCode() + "]：" + response.getMsg());
            }
        } catch (AlipayApiException e) {
            log.error("支付宝订单关闭API调用异常，订单号：{}，错误：{}", orderNo, e.getErrMsg(), e);
            throw new RuntimeException("支付宝订单关闭异常：" + e.getErrMsg());
        } catch (Exception e) {
            log.error("JSON序列化异常", e);
            throw new RuntimeException("订单数据序列化失败");
        }
    }

    @Override
    public String refund(String orderNo, String refundRequestNo, String refundAmount, String reason) {
        validateAlipayConfig();
        
        try {
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", orderNo);
            bizContent.put("out_request_no", refundRequestNo);
            bizContent.put("refund_amount", new BigDecimal(refundAmount));
            if (reason != null && !reason.isEmpty()) {
                bizContent.put("refund_reason", reason);
            }
            
            Map<String, Object> queryOptions = new HashMap<>();
            queryOptions.put("query_options", new String[]{"deposit_back_info"});
            bizContent.putAll(queryOptions);

            request.setBizContent(objectMapper.writeValueAsString(bizContent));

            AlipayTradeRefundResponse response = createAlipayClient().execute(request);

            if (response.isSuccess()) {
                log.info("支付宝退款成功，订单号：{}，退款请求号：{}", orderNo, refundRequestNo);
                return response.getTradeNo();
            } else {
                log.error("支付宝退款失败，订单号：{}，退款请求号：{}，错误码：{}，错误信息：{}",
                        orderNo, refundRequestNo, response.getCode(), response.getMsg());
                throw new RuntimeException("支付宝退款失败[" + response.getCode() + "]：" + response.getMsg());
            }
        } catch (AlipayApiException e) {
            log.error("支付宝退款API调用异常，订单号：{}，退款请求号：{}，错误：{}", orderNo, refundRequestNo, e.getErrMsg(), e);
            throw new RuntimeException("支付宝退款异常：" + e.getErrMsg());
        } catch (Exception e) {
            log.error("JSON序列化异常", e);
            throw new RuntimeException("订单数据序列化失败");
        }
    }

    @Override
    public String queryRefund(String orderNo, String refundRequestNo) {
        validateAlipayConfig();
        
        try {
            AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
            
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", orderNo);
            bizContent.put("out_request_no", refundRequestNo);

            request.setBizContent(objectMapper.writeValueAsString(bizContent));

            AlipayTradeFastpayRefundQueryResponse response = createAlipayClient().execute(request);

            if (response.isSuccess()) {
                log.info("支付宝退款查询成功，订单号：{}，退款请求号：{}，退款状态：{}", 
                        orderNo, refundRequestNo, response.getRefundStatus());
                return objectMapper.writeValueAsString(response);
            } else {
                log.error("支付宝退款查询失败，订单号：{}，退款请求号：{}，错误码：{}，错误信息：{}",
                        orderNo, refundRequestNo, response.getCode(), response.getMsg());
                throw new RuntimeException("支付宝退款查询失败[" + response.getCode() + "]：" + response.getMsg());
            }
        } catch (AlipayApiException e) {
            log.error("支付宝退款查询API调用异常，订单号：{}，退款请求号：{}，错误：{}", orderNo, refundRequestNo, e.getErrMsg(), e);
            throw new RuntimeException("支付宝退款查询异常：" + e.getErrMsg());
        } catch (Exception e) {
            log.error("JSON序列化异常", e);
            throw new RuntimeException("订单数据序列化失败");
        }
    }

    @Override
    public String queryBillDownloadUrl(String billType, String billDate) {
        validateAlipayConfig();
        
        try {
            AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
            
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("bill_type", billType);
            bizContent.put("bill_date", billDate);

            request.setBizContent(objectMapper.writeValueAsString(bizContent));

            AlipayDataDataserviceBillDownloadurlQueryResponse response = createAlipayClient().execute(request);

            if (response.isSuccess()) {
                log.info("支付宝账单下载地址查询成功，账单类型：{}，账单日期：{}", billType, billDate);
                return response.getBillDownloadUrl();
            } else {
                log.error("支付宝账单下载地址查询失败，账单类型：{}，账单日期：{}，错误码：{}，错误信息：{}",
                        billType, billDate, response.getCode(), response.getMsg());
                throw new RuntimeException("支付宝账单下载地址查询失败[" + response.getCode() + "]：" + response.getMsg());
            }
        } catch (AlipayApiException e) {
            log.error("支付宝账单查询API调用异常，账单类型：{}，账单日期：{}，错误：{}", billType, billDate, e.getErrMsg(), e);
            throw new RuntimeException("支付宝账单查询异常：" + e.getErrMsg());
        } catch (Exception e) {
            log.error("JSON序列化异常", e);
            throw new RuntimeException("订单数据序列化失败");
        }
    }

    private AlipayClient createAlipayClient() {
        return new DefaultAlipayClient(
                alipayConfig.getGateway(),
                alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(),
                "json",
                "UTF-8",
                alipayConfig.getPublicKey(),
                "RSA2"
        );
    }

    private void validateAlipayConfig() {
        if (isBlank(alipayConfig.getAppId())) {
            throw new RuntimeException("支付宝配置错误：APP_ID为空");
        }
        if (isBlank(alipayConfig.getPrivateKey())) {
            throw new RuntimeException("支付宝配置错误：私钥为空");
        }
        if (isBlank(alipayConfig.getPublicKey())) {
            throw new RuntimeException("支付宝配置错误：公钥为空");
        }
        if (isBlank(alipayConfig.getGateway())) {
            throw new RuntimeException("支付宝配置错误：网关地址为空");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    @Override
    public boolean verifyNotification(Map<String, String> params) {
        try {
            return AlipaySignature.rsaCheckV1(
                    params,
                    alipayConfig.getPublicKey(),
                    "UTF-8",
                    "RSA2"
            );
        } catch (AlipayApiException e) {
            log.error("支付宝签名验证异常", e);
            return false;
        }
    }

    @Override
    public boolean verifyRefundDepositBackNotification(Map<String, String> params) {
        try {
            return AlipaySignature.rsaCheckV1(
                    params,
                    alipayConfig.getPublicKey(),
                    "UTF-8",
                    "RSA2"
            );
        } catch (AlipayApiException e) {
            log.error("支付宝退款到银行卡回调签名验证异常", e);
            return false;
        }
    }
}