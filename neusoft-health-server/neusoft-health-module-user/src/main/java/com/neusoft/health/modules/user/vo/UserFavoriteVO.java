package com.neusoft.health.modules.user.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserFavoriteVO {

    private Long id;

    private Long messageId;

    private String content;

    private String sessionTitle;

    private Integer role;

    private LocalDateTime createdTime;
}
