package com.neusoft.health.modules.member.vo;

import lombok.Data;

@Data
public class InviteInfoVO {
    private String inviteCode;
    private String inviteUrl;
    private Integer invitedCount;
    private Integer rewardDays;
}