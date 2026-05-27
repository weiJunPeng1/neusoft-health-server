package com.neusoft.health.modules.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.health.common.enums.PaymentStatusEnum;
import com.neusoft.health.common.exception.BusinessException;
import com.neusoft.health.common.result.ResultCode;
import com.neusoft.health.modules.member.dto.OrderCreateDTO;
import com.neusoft.health.modules.member.entity.PaymentOrder;
import com.neusoft.health.modules.member.entity.SubscriptionPlan;
import com.neusoft.health.modules.member.mapper.PaymentOrderMapper;
import com.neusoft.health.modules.member.mapper.SubscriptionPlanMapper;
import com.neusoft.health.modules.member.service.InviteService;
import com.neusoft.health.modules.member.service.MemberService;
import com.neusoft.health.modules.member.service.PaymentService;
import com.neusoft.health.modules.member.vo.PaymentOrderVO;
import com.neusoft.health.modules.member.vo.SubscriptionPlanVO;
import com.neusoft.health.common.entity.User;
import com.neusoft.health.common.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final SubscriptionPlanMapper planMapper;
    private final PaymentOrderMapper orderMapper;
    private final UserMapper userMapper;
    private final MemberService memberService;
    private final InviteService inviteService;

    @Override
    public List<SubscriptionPlanVO> listPlans(Long userId) {
        User user = userMapper.selectById(userId);
        boolean isFirst = user != null && (user.getFirstPurchase() == null || user.getFirstPurchase() == 0);
        List<SubscriptionPlan> plans = planMapper.selectList(
                new LambdaQueryWrapper<SubscriptionPlan>().eq(SubscriptionPlan::getStatus, 1).orderByAsc(SubscriptionPlan::getSortOrder));
        return plans.stream().map(p -> {
            SubscriptionPlanVO vo = new SubscriptionPlanVO();
            vo.setId(p.getId());
            vo.setPlanCode(p.getPlanCode());
            vo.setPlanName(p.getPlanName());
            vo.setLevelCode(p.getLevelCode());
            vo.setDurationDays(p.getDurationDays());
            if (isFirst && p.getOriginalPrice() != null) {
                vo.setPrice(p.getOriginalPrice());
                vo.setOriginalPrice(p.getPrice());
                vo.setIsFirstPurchasePrice(true);
            } else {
                vo.setPrice(p.getPrice());
                vo.setOriginalPrice(p.getOriginalPrice());
                vo.setIsFirstPurchasePrice(false);
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentOrderVO createOrder(Long userId, OrderCreateDTO dto) {
        SubscriptionPlan plan;
        if (dto.getPlanId() != null) {
            plan = planMapper.selectById(dto.getPlanId());
        } else if (dto.getPlanCode() != null) {
            plan = planMapper.selectOne(new LambdaQueryWrapper<SubscriptionPlan>().eq(SubscriptionPlan::getPlanCode, dto.getPlanCode()));
        } else {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        if (plan == null || plan.getStatus() == 0) {
            throw new BusinessException(ResultCode.PLAN_NOT_FOUND);
        }
        User user = userMapper.selectById(userId);
        boolean isFirst = user != null && (user.getFirstPurchase() == null || user.getFirstPurchase() == 0);
        BigDecimal amount = isFirst && plan.getOriginalPrice() != null ? plan.getOriginalPrice() : plan.getPrice();

        PaymentOrder existingOrder = orderMapper.selectOne(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getUserId, userId)
                        .eq(PaymentOrder::getPlanId, plan.getId())
                        .eq(PaymentOrder::getAmount, amount)
                        .eq(PaymentOrder::getPayStatus, PaymentStatusEnum.PENDING.getCode())
                        .gt(PaymentOrder::getExpireTime, LocalDateTime.now())
                        .last("LIMIT 1"));
        if (existingOrder != null) {
            return toVO(existingOrder, plan.getPlanName());
        }

        String orderNo = "MP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + String.format("%06d", System.nanoTime() % 1000000);
        PaymentOrder order = new PaymentOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setPlanId(plan.getId());
        order.setAmount(amount);
        order.setPayMethod("alipay");
        order.setPayStatus(0);
        order.setExpireTime(LocalDateTime.now().plusMinutes(15));
        orderMapper.insert(order);
        return toVO(order, plan.getPlanName());
    }

    @Override
    public PaymentOrderVO queryOrder(String orderNo) {
        PaymentOrder order = orderMapper.selectOne(new LambdaQueryWrapper<PaymentOrder>().eq(PaymentOrder::getOrderNo, orderNo));
        if (order == null) throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        SubscriptionPlan plan = planMapper.selectById(order.getPlanId());
        return toVO(order, plan != null ? plan.getPlanName() : "");
    }

    @Override
    @Transactional
    public void cancelOrder(String orderNo, Long userId) {
        PaymentOrder order = orderMapper.selectOne(new LambdaQueryWrapper<PaymentOrder>().eq(PaymentOrder::getOrderNo, orderNo));
        if (order == null) throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        if (!order.getUserId().equals(userId)) throw new BusinessException(ResultCode.FORBIDDEN);
        if (order.getPayStatus() != 0) throw new BusinessException(ResultCode.ORDER_ALREADY_PAID);
        order.setPayStatus(2);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void handleAlipayCallback(String orderNo, String transactionId) {
        PaymentOrder order = orderMapper.selectOne(new LambdaQueryWrapper<PaymentOrder>().eq(PaymentOrder::getOrderNo, orderNo));
        if (order == null) { log.error("Order not found: {}", orderNo); return; }
        if (order.getPayStatus() != 0) { log.warn("Order {} already processed", orderNo); return; }
        order.setPayStatus(1);
        order.setTransactionId(transactionId);
        order.setPaidTime(LocalDateTime.now());
        orderMapper.updateById(order);

        SubscriptionPlan plan = planMapper.selectById(order.getPlanId());
        if (plan == null) { log.error("Plan not found for order {}", orderNo); return; }
        memberService.activateMembership(order.getUserId(), plan.getLevelCode(), plan.getDurationDays(), order.getId());
        inviteService.processInviteReward(order.getUserId(), plan.getDurationDays());
        log.info("Payment callback processed: orderNo={}, userId={}, level={}, days={}", orderNo, order.getUserId(), plan.getLevelCode(), plan.getDurationDays());
    }

    private PaymentOrderVO toVO(PaymentOrder order, String planName) {
        PaymentOrderVO vo = new PaymentOrderVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setPlanId(order.getPlanId());
        vo.setPlanName(planName);
        vo.setAmount(order.getAmount());
        vo.setPayMethod(order.getPayMethod());
        vo.setPayStatus(order.getPayStatus());
        vo.setPayStatusDesc(PaymentStatusEnum.getDescByCode(order.getPayStatus()));
        vo.setPaidTime(order.getPaidTime());
        vo.setExpireTime(order.getExpireTime());
        vo.setCreatedTime(order.getCreatedTime());
        return vo;
    }

    @Override
    public List<PaymentOrderVO> listOrders(Long userId) {
        List<PaymentOrder> orders = orderMapper.selectList(
                new LambdaQueryWrapper<PaymentOrder>().eq(PaymentOrder::getUserId, userId).orderByDesc(PaymentOrder::getCreatedTime));
        return orders.stream().map(order -> {
            SubscriptionPlan plan = planMapper.selectById(order.getPlanId());
            return toVO(order, plan != null ? plan.getPlanName() : "");
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void simulatePayment(String orderNo) {
        handleAlipayCallback(orderNo, "SIM_" + System.currentTimeMillis());
    }
}