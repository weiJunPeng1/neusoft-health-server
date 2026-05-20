package com.neusoft.health.modules.user.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserSettingUpdateDTO {

    private Integer notificationEnabled;

    private BigDecimal voiceSpeed;

    private Integer voiceVolume;

    private String voiceTone;

    private Integer anonymousMode;

    private Integer recommendEnabled;
}
