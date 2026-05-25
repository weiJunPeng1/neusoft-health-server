package com.neusoft.health.framework.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;
import java.util.List;

/**
 * JWT认证过滤器
 * <p>
 * 从请求中提取JWT令牌，验证令牌有效性，并设置Spring Security的认证上下文。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWT令牌提供者
     */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 执行过滤逻辑
     *
     * @param request     HTTP请求
     * @param response    HTTP响应
     * @param filterChain 过滤链
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = resolveToken(request);
        if (StringUtils.hasText(token)) {
            try {
                Claims claims = jwtTokenProvider.parseToken(token);
                Long userId = claims.get("userId", Long.class);
                String roleCode = claims.get("roleCode", String.class);
                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + roleCode));
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, token, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException e) {
                request.setAttribute("jwtExpired", true);
            } catch (Exception e) {
                request.setAttribute("jwtInvalid", true);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中解析JWT令牌
     *
     * @param request HTTP请求
     * @return JWT令牌，未找到返回null
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
