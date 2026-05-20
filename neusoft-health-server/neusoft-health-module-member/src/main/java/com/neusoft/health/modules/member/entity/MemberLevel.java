package com.neusoft.health.modules.member.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会员等级定义实体
 */
@Data
@TableName("member_levels")
public class MemberLevel {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String levelCode;

    private String levelName;

    private Integer dailyQuota;

    private Integer contextRounds;

    private Integer autoSync;

    private Integer deepAnalysis;

    private Integer exportEnabled;

    private Integer shareLimit;

    private Integer sortOrder;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @TableLogic
    private Integer isDeleted;
}