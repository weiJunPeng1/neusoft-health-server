package com.neusoft.health.modules.consultation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("health_search_cache")
public class HealthSearchCache {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String keyword;

    private String keywordHash;

    private String content;

    private Integer source;

    private Integer hitCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @TableLogic
    private Integer isDeleted;
}
