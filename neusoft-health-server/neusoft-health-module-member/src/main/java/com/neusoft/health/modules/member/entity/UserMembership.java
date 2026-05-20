package com.neusoft.health.modules.member.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_memberships")
public class UserMembership {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String levelCode;
    private LocalDateTime startTime;
    private LocalDateTime expireTime;
    private LocalDateTime graceEndTime;
    private Long sourceOrderId;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    @TableLogic
    private Integer isDeleted;
}