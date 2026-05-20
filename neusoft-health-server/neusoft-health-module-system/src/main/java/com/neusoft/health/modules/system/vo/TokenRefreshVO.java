package com.neusoft.health.modules.system.vo;

import lombok.Data;

@Data
public class TokenRefreshVO {

    private String accessToken;

    private String refreshToken;

    private Long expiresIn;
}
