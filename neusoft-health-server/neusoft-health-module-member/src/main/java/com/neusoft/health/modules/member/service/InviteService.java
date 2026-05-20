package com.neusoft.health.modules.member.service;

import com.neusoft.health.modules.member.vo.InviteInfoVO;

public interface InviteService {
    String generateInviteCode(Long userId);
    void bindInviteRelation(Long userId, String inviteCode);
    void processInviteReward(Long userId, int paidDays);
    InviteInfoVO getInviteInfo(Long userId);
}