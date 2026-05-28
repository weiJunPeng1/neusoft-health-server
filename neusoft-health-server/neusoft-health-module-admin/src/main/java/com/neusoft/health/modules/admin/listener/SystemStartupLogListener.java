package com.neusoft.health.modules.admin.listener;

import com.neusoft.health.modules.admin.entity.OperationLog;
import com.neusoft.health.modules.admin.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class SystemStartupLogListener implements ApplicationRunner {

    private final OperationLogMapper operationLogMapper;

    @Override
    public void run(ApplicationArguments args) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            int port = 8080;

            OperationLog operationLog = new OperationLog();
            operationLog.setUserId(1L);
            operationLog.setUsername("SYSTEM");
            operationLog.setModule("系统");
            operationLog.setOperation("系统启动");
            operationLog.setTargetType("Application");
            operationLog.setRequestMethod("STARTUP");
            operationLog.setRequestUrl("/");
            operationLog.setIpAddress(host + ":" + port);
            operationLog.setStatus(1);
            operationLog.setIsDeleted(0);
            operationLog.setRequestParams("系统启动完成");
            operationLogMapper.insert(operationLog);

            log.info("系统启动日志已记录: host={}", host);
        } catch (Exception e) {
            log.warn("系统启动日志记录失败: {}", e.getMessage());
        }
    }
}
