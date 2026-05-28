package com.neusoft.health.modules.admin.listener;

import com.neusoft.health.common.entity.User;
import com.neusoft.health.common.event.UserLoginEvent;
import com.neusoft.health.common.mapper.UserMapper;
import com.neusoft.health.modules.admin.entity.OperationLog;
import com.neusoft.health.modules.admin.mapper.OperationLogMapper;
import com.neusoft.health.modules.security.entity.UserRole;
import com.neusoft.health.modules.security.mapper.UserRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminLoginEventListener {

    private final OperationLogMapper operationLogMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    @Async
    @EventListener
    public void handleUserLogin(UserLoginEvent event) {
        try {
            List<UserRole> userRoles = userRoleMapper.selectList(
                    new LambdaQueryWrapper<UserRole>()
                            .eq(UserRole::getUserId, event.getUserId()));

            boolean isAdmin = userRoles.stream().anyMatch(ur -> {
                Long roleId = ur.getRoleId();
                return roleId != null && (roleId == 2 || roleId == 3 || roleId == 4);
            });

            if (!isAdmin) {
                return;
            }

            OperationLog operationLog = new OperationLog();
            operationLog.setUserId(event.getUserId());
            operationLog.setUsername(event.getNickname());
            operationLog.setModule("系统");
            operationLog.setOperation("管理员登录");
            operationLog.setTargetType("User");
            operationLog.setTargetId(String.valueOf(event.getUserId()));
            operationLog.setRequestMethod("LOGIN");
            operationLog.setRequestUrl("/api/v1/auth/login");
            operationLog.setIpAddress(event.getClientIp());
            operationLog.setUserAgent(event.getUserAgent());
            operationLog.setRequestParams("登录方式: " + event.getLoginType());
            operationLog.setStatus(1);
            operationLog.setIsDeleted(0);
            operationLogMapper.insert(operationLog);

            log.info("管理员登录日志已记录: userId={}, nickname={}", event.getUserId(), event.getNickname());
        } catch (Exception e) {
            log.warn("管理员登录日志记录失败: {}", e.getMessage());
        }
    }
}
