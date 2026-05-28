package com.neusoft.health.modules.member.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberUserVO {

    private Long id;

    private String nickname;

    private String memberLevel;

    private LocalDateTime startTime;

    private LocalDateTime expireTime;

    private Integer status;
}
