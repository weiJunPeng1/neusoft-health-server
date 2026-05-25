package com.neusoft.health.modules.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.common.exception.BusinessException;
import com.neusoft.health.common.result.ResultCode;
import com.neusoft.health.modules.member.dto.RefundApplyDTO;
import com.neusoft.health.modules.member.entity.PaymentOrder;
import com.neusoft.health.modules.member.entity.RefundRequest;
import com.neusoft.health.modules.member.entity.SubscriptionPlan;
import com.neusoft.health.modules.member.entity.UserMembership;
import com.neusoft.health.modules.member.mapper.PaymentOrderMapper;
import com.neusoft.health.modules.member.mapper.RefundRequestMapper;
import com.neusoft.health.modules.member.mapper.SubscriptionPlanMapper;
import com.neusoft.health.modules.member.mapper.UserMembershipMapper;
import com.neusoft.health.modules.member.service.MemberService;
import com.neusoft.health.modules.member.service.RefundService;
import com.neusoft.health.modules.member.vo.RefundRequestVO;
import com.neusoft.health.common.entity.User;
import com.neusoft.health.common.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundServiceImpl extends ServiceImpl<RefundRequestMapper, RefundRequest> implements RefundService {

    private final RefundRequestMapper refundRequestMapper;
    private final PaymentOrderMapper paymentOrderMapper;
    private final SubscriptionPlanMapper subscriptionPlanMapper;
    private final UserMembershipMapper userMembershipMapper;
    private final MemberService memberService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void applyRefund(Long userId, RefundApplyDTO dto) {
        PaymentOrder order = paymentOrderMapper.selectOne(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getOrderNo, dto.getOrderNo()));
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        if (order.getPayStatus() != 1) {
            throw new BusinessException(ResultCode.ORDER_STATUS_INVALID);
        }

        SubscriptionPlan plan = subscriptionPlanMapper.selectById(order.getPlanId());
        if (plan != null && plan.getOriginalPrice() != null && order.getAmount().compareTo(plan.getOriginalPrice()) == 0) {
            throw new BusinessException(ResultCode.REFUND_NOT_ALLOWED);
        }

        long existingRefunds = count(new LambdaQueryWrapper<RefundRequest>()
                .eq(RefundRequest::getOrderId, order.getId())
                .eq(RefundRequest::getStatus, 0));
        if (existingRefunds > 0) {
            throw new BusinessException(ResultCode.ORDER_STATUS_INVALID);
        }

        BigDecimal refundAmount = calculateRefundAmount(order);

        RefundRequest refund = new RefundRequest();
        refund.setOrderId(order.getId());
        refund.setUserId(userId);
        refund.setReason(dto.getReason());
        refund.setRefundAmount(refundAmount);
        refund.setStatus(0);
        refundRequestMapper.insert(refund);

        order.setPayStatus(3);
        paymentOrderMapper.updateById(order);

        log.info("Refund request created: userId={}, orderId={}, amount={}", userId, order.getId(), refundAmount);
    }

    @Override
    @Transactional
    public void approveRefund(Long refundId, Long adminId, String remark) {
        RefundRequest refund = refundRequestMapper.selectById(refundId);
        if (refund == null) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        if (refund.getStatus() != 0) {
            throw new BusinessException(ResultCode.REFUND_ALREADY_HANDLED);
        }

        PaymentOrder order = paymentOrderMapper.selectById(refund.getOrderId());
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        User user = userMapper.selectById(refund.getUserId());
        if (user != null) {
            user.setMemberLevel("L0");
            user.setMemberExpireTime(null);
            userMapper.updateById(user);
        }

        List<UserMembership> memberships = userMembershipMapper.selectList(
                new LambdaQueryWrapper<UserMembership>()
                        .eq(UserMembership::getUserId, refund.getUserId())
                        .orderByDesc(UserMembership::getCreatedTime)
        );
        if (!memberships.isEmpty()) {
            UserMembership latest = memberships.get(0);
            latest.setStatus(0);
            userMembershipMapper.updateById(latest);
        }

        order.setPayStatus(4);
        paymentOrderMapper.updateById(order);

        refund.setStatus(1);
        refund.setHandledBy(adminId);
        refund.setHandleRemark(remark);
        refund.setHandledTime(LocalDateTime.now());
        refundRequestMapper.updateById(refund);
        log.info("Refund approved: refundId={}, adminId={}", refundId, adminId);
    }

    @Override
    @Transactional
    public void rejectRefund(Long refundId, Long adminId, String remark) {
        RefundRequest refund = refundRequestMapper.selectById(refundId);
        if (refund == null) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        if (refund.getStatus() != 0) {
            throw new BusinessException(ResultCode.REFUND_ALREADY_HANDLED);
        }

        refund.setStatus(2);
        refund.setHandledBy(adminId);
        refund.setHandleRemark(remark);
        refund.setHandledTime(LocalDateTime.now());
        refundRequestMapper.updateById(refund);

        PaymentOrder order = paymentOrderMapper.selectById(refund.getOrderId());
        if (order != null) {
            order.setPayStatus(1);
            paymentOrderMapper.updateById(order);
        }

        log.info("Refund rejected: refundId={}, adminId={}", refundId, adminId);
    }

    @Override
    public List<RefundRequestVO> getRefundList() {
        List<RefundRequest> refunds = list(new LambdaQueryWrapper<RefundRequest>()
                .orderByDesc(RefundRequest::getCreatedTime));
        return refunds.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<RefundRequestVO> getUserRefundList(Long userId) {
        List<RefundRequest> refunds = list(new LambdaQueryWrapper<RefundRequest>()
                .eq(RefundRequest::getUserId, userId)
                .orderByDesc(RefundRequest::getCreatedTime));
        return refunds.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void handleDepositBackCallback(String orderNo, String refundRequestNo, String depositBackStatus) {
        PaymentOrder order = paymentOrderMapper.selectOne(
                new LambdaQueryWrapper<PaymentOrder>().eq(PaymentOrder::getOrderNo, orderNo));
        
        if (order == null) {
            log.warn("Deposit back callback: order not found, orderNo={}", orderNo);
            return;
        }

        RefundRequest refund = refundRequestMapper.selectOne(
                new LambdaQueryWrapper<RefundRequest>()
                        .eq(RefundRequest::getOrderId, order.getId())
                        .eq(RefundRequest::getStatus, 1));

        if (refund == null) {
            log.warn("Deposit back callback: refund not found, orderNo={}, refundRequestNo={}", orderNo, refundRequestNo);
            return;
        }

        if ("SUCCESS".equals(depositBackStatus)) {
            refund.setStatus(3);
            refund.setHandleRemark("退款已到账（银行卡）");
            refund.setHandledTime(LocalDateTime.now());
            refundRequestMapper.updateById(refund);
            log.info("Deposit back success: orderNo={}, refundId={}", orderNo, refund.getId());
        } else if ("FAIL".equals(depositBackStatus)) {
            refund.setStatus(4);
            refund.setHandleRemark("退款到银行卡失败");
            refund.setHandledTime(LocalDateTime.now());
            refundRequestMapper.updateById(refund);
            log.warn("Deposit back failed: orderNo={}, refundId={}", orderNo, refund.getId());
        }
    }

    private BigDecimal calculateRefundAmount(PaymentOrder order) {
        SubscriptionPlan plan = subscriptionPlanMapper.selectById(order.getPlanId());
        if (plan == null || order.getPaidTime() == null) {
            throw new BusinessException(ResultCode.REFUND_AMOUNT_ERROR);
        }

        long totalDays = plan.getDurationDays();
        long usedDays = ChronoUnit.DAYS.between(order.getPaidTime(), LocalDateTime.now());
        if (usedDays < 0) usedDays = 0;
        long remainingDays = totalDays - usedDays;
        if (remainingDays < 0) remainingDays = 0;

        BigDecimal ratio = BigDecimal.valueOf(remainingDays).divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);
        return order.getAmount().multiply(ratio).setScale(2, RoundingMode.HALF_UP);
    }

    private RefundRequestVO toVO(RefundRequest refund) {
        RefundRequestVO vo = new RefundRequestVO();
        vo.setId(refund.getId());
        vo.setOrderId(refund.getOrderId());
        vo.setUserId(refund.getUserId());
        vo.setReason(refund.getReason());
        vo.setRefundAmount(refund.getRefundAmount());
        vo.setStatus(refund.getStatus());
        switch (refund.getStatus()) {
            case 0: vo.setStatusDesc("待审核"); break;
            case 1: vo.setStatusDesc("已通过"); break;
            case 2: vo.setStatusDesc("已拒绝"); break;
            case 3: vo.setStatusDesc("已到账"); break;
            case 4: vo.setStatusDesc("到账失败"); break;
        }
        vo.setHandledBy(refund.getHandledBy());
        vo.setHandleRemark(refund.getHandleRemark());
        vo.setHandledTime(refund.getHandledTime());
        vo.setCreatedTime(refund.getCreatedTime());
        vo.setUpdatedTime(refund.getUpdatedTime());

        PaymentOrder order = paymentOrderMapper.selectById(refund.getOrderId());
        if (order != null) {
            vo.setOrderNo(order.getOrderNo());
        }

        User user = userMapper.selectById(refund.getUserId());
        if (user != null) {
            vo.setUserPhone(user.getPhoneEncrypted());
        }

        return vo;
    }
}