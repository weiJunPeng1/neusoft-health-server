package com.neusoft.health.modules.member.vo;

import lombok.Data;

@Data
public class MemberLevelVO {
    private String levelCode;
    private String levelName;
    private Integer dailyQuota;
    private Integer contextRounds;
    private Boolean autoSync;
    private Boolean deepAnalysis;
    private Boolean exportEnabled;
    private String description;
}
