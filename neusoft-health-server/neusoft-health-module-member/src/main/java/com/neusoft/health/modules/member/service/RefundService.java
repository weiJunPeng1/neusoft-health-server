package com.neusoft.health.modules.member.service;

import com.neusoft.health.modules.member.dto.RefundApplyDTO;
import com.neusoft.health.modules.member.vo.RefundRequestVO;
import java.util.List;

public interface RefundService {
    void applyRefund(Long userId, RefundApplyDTO dto);
    void approveRefund(Long refundId, Long adminId, String remark);
    void rejectRefund(Long refundId, Long adminId, String remark);
    List<RefundRequestVO> getRefundList();
    List<RefundRequestVO> getUserRefundList(Long userId);
    void handleDepositBackCallback(String orderNo, String refundRequestNo, String depositBackStatus);
}