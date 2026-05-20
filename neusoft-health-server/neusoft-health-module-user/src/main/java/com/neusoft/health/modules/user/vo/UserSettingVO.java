package com.neusoft.health.modules.user.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserSettingVO {

    private Long id;

    private Long userId;

    private Integer notificationEnabled;

    private BigDecimal voiceSpeed;

    private Integer voiceVolume;

    private String voiceTone;

    private Integer anonymousMode;

    private Integer recommendEnabled;

    private LocalDateTime updatedTime;
}
