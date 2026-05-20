package com.neusoft.health.modules.member.service;

import com.neusoft.health.modules.member.dto.OrderCreateDTO;
import com.neusoft.health.modules.member.vo.PaymentOrderVO;
import com.neusoft.health.modules.member.vo.SubscriptionPlanVO;
import java.util.List;

public interface PaymentService {
    List<SubscriptionPlanVO> listPlans(Long userId);
    PaymentOrderVO createOrder(Long userId, OrderCreateDTO dto);
    PaymentOrderVO queryOrder(String orderNo);
    void cancelOrder(String orderNo, Long userId);
    void handleAlipayCallback(String orderNo, String transactionId);
}