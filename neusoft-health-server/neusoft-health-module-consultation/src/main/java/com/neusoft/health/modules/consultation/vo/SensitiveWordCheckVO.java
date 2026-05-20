package com.neusoft.health.modules.consultation.vo;

import lombok.Data;
import java.util.List;

@Data
public class SensitiveWordCheckVO {

    private boolean hit;

    private List<String> words;
}
