package com.neusoft.health.modules.member.service;

public interface AlipayService {
    String generateQrCode(String orderNo, String subject, String amount);

    boolean verifyNotification(java.util.Map<String, String> params);

    String createPagePayUrl(String orderNo, String subject, String amount);

    String queryOrder(String orderNo);

    String closeOrder(String orderNo);

    String refund(String orderNo, String refundRequestNo, String refundAmount, String reason);

    String queryRefund(String orderNo, String refundRequestNo);

    String queryBillDownloadUrl(String billType, String billDate);

    boolean verifyRefundDepositBackNotification(java.util.Map<String, String> params);
}