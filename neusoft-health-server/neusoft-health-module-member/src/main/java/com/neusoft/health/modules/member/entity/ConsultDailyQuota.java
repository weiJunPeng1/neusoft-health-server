package com.neusoft.health.modules.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("consult_daily_quota")
public class ConsultDailyQuota {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private LocalDate consultDate;
    
    private Integer consultCount;
    
    private LocalDateTime createdTime;
    
    private LocalDateTime updatedTime;
}
