package com.neusoft.health.modules.admin.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neusoft.health.framework.util.SecurityUtil;
import com.neusoft.health.common.annotation.AdminOperation;
import com.neusoft.health.common.entity.User;
import com.neusoft.health.common.mapper.UserMapper;
import com.neusoft.health.modules.admin.entity.OperationLog;
import com.neusoft.health.modules.admin.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AdminOperationAspect {

    private final OperationLogMapper operationLogMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    private static final ExpressionParser SPEL_PARSER = new SpelExpressionParser();

    @Around("@annotation(adminOperation)")
    public Object around(ProceedingJoinPoint joinPoint, AdminOperation adminOperation) throws Throwable {
        long startTime = System.currentTimeMillis();
        OperationLog operationLog = buildLog(joinPoint, adminOperation);
        Object result = null;
        try {
            result = joinPoint.proceed();
            operationLog.setStatus(1);
            return result;
        } catch (Throwable ex) {
            operationLog.setStatus(0);
            operationLog.setErrorMsg(ex.getMessage() != null
                    ? ex.getMessage().substring(0, Math.min(ex.getMessage().length(), 500))
                    : ex.getClass().getSimpleName());
            throw ex;
        } finally {
            operationLog.setDuration((int) (System.currentTimeMillis() - startTime));
            try {
                operationLogMapper.insert(operationLog);
            } catch (Exception e) {
                log.error("操作日志记录失败: {}", e.getMessage());
            }
        }
    }

    private OperationLog buildLog(ProceedingJoinPoint joinPoint, AdminOperation adminOperation) {
        OperationLog operationLog = new OperationLog();
        operationLog.setModule(adminOperation.module());
        operationLog.setOperation(adminOperation.operation());
        operationLog.setTargetType(adminOperation.targetType());
        operationLog.setIsDeleted(0);

        Long userId = SecurityUtil.getCurrentUserId();
        operationLog.setUserId(userId);
        if (userId != null) {
            User user = userMapper.selectById(userId);
            if (user != null) {
                operationLog.setUsername(user.getNickname());
            }
        }

        String targetId = resolveTargetId(joinPoint, adminOperation.targetIdParam());
        operationLog.setTargetId(targetId);

        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operationLog.setRequestMethod(request.getMethod());
                operationLog.setRequestUrl(request.getRequestURI());
                operationLog.setIpAddress(getClientIp(request));
                operationLog.setUserAgent(request.getHeader("User-Agent"));
            }
        } catch (Exception e) {
            log.debug("获取请求信息失败: {}", e.getMessage());
        }

        try {
            String params = objectMapper.writeValueAsString(joinPoint.getArgs());
            operationLog.setRequestParams(truncate(params, 2000));
        } catch (Exception e) {
            operationLog.setRequestParams("{}");
        }

        return operationLog;
    }

    private String resolveTargetId(ProceedingJoinPoint joinPoint, String paramName) {
        if (paramName == null || paramName.isEmpty()) {
            return null;
        }
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Parameter[] parameters = method.getParameters();
            Object[] args = joinPoint.getArgs();

            StandardEvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < parameters.length; i++) {
                context.setVariable(parameters[i].getName(), args[i]);
            }
            Object value = SPEL_PARSER.parseExpression(paramName).getValue(context);
            return value != null ? String.valueOf(value) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String truncate(String str, int maxLen) {
        if (str == null) return null;
        return str.length() > maxLen ? str.substring(0, maxLen) + "..." : str;
    }
}
