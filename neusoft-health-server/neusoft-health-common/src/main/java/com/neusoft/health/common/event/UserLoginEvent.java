package com.neusoft.health.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserLoginEvent extends ApplicationEvent {

    private final Long userId;
    private final String nickname;
    private final String loginType;
    private final String clientIp;
    private final String userAgent;

    public UserLoginEvent(Object source, Long userId, String nickname,
                          String loginType, String clientIp, String userAgent) {
        super(source);
        this.userId = userId;
        this.nickname = nickname;
        this.loginType = loginType;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
    }
}
