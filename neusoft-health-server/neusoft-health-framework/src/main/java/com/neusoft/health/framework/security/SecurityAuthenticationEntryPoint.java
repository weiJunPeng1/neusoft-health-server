package com.neusoft.health.framework.security;

import com.neusoft.health.common.result.R;
import com.neusoft.health.common.result.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 未认证入口点
 * <p>
 * 当用户未登录或登录已过期时，返回401未授权响应。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Component
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * JSON对象映射器
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理未认证的请求
     *
     * @param request        HTTP请求
     * @param response       HTTP响应
     * @param authException  认证异常
     * @throws IOException IO异常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(R.fail(ResultCode.UNAUTHORIZED)));
        writer.flush();
    }
}
