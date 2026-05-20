package com.neusoft.health.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户收藏表（user_favorites）
 * <p>
 * 记录用户收藏的有用AI回复消息。通过 user_id + message_id 构成唯一约束，
 * 防止重复收藏同一消息。删除为逻辑删除，取消收藏即软删除该记录。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@TableName("user_favorites")
public class UserFavorite {

    /** 收藏ID（bigint unsigned 自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（关联users.id） */
    private Long userId;

    /** 消息ID（关联messages.id） */
    private Long messageId;

    /** 创建时间（自动填充，即收藏时间） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /** 逻辑删除：0=否，1=是 */
    @TableLogic
    private Integer isDeleted;
}
