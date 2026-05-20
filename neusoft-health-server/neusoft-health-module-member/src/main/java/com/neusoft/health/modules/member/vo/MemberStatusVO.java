package com.neusoft.health.modules.member.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MemberStatusVO {
    private String levelCode;
    private String levelName;
    private Integer dailyQuota;
    private Integer contextRounds;
    private Boolean autoSync;
    private Boolean deepAnalysis;
    private Boolean exportEnabled;
    private String badgeIcon;
    private LocalDateTime expireTime;
    private Integer remainingDays;
    private Boolean inGracePeriod;
}