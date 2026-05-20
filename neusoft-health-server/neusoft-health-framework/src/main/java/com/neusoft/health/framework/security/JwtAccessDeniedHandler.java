package com.neusoft.health.framework.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.neusoft.health.common.result.R;
import com.neusoft.health.common.result.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 访问拒绝处理器
 * <p>
 * 当用户没有权限访问资源时，返回403禁止访问响应。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * JSON对象映射器
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理访问被拒绝的请求
     *
     * @param request               HTTP请求
     * @param response              HTTP响应
     * @param accessDeniedException 访问拒绝异常
     * @throws IOException IO异常
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(R.fail(ResultCode.FORBIDDEN)));
        writer.flush();
    }
}
