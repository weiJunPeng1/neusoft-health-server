package com.neusoft.health.framework.config;

import com.neusoft.health.framework.security.JwtAccessDeniedHandler;
import com.neusoft.health.framework.security.JwtAuthenticationFilter;
import com.neusoft.health.framework.security.SecurityAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security安全配置
 * <p>
 * 配置安全过滤器链、JWT认证、权限控制等安全相关功能。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * JWT认证过滤器
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    /**
     * 认证入口点
     */
    private final SecurityAuthenticationEntryPoint authenticationEntryPoint;
    /**
     * 访问拒绝处理器
     */
    private final JwtAccessDeniedHandler accessDeniedHandler;

    /**
     * 配置安全过滤器链
     *
     * @param http HTTP安全配置
     * @return 安全过滤器链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/home",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/doc.html"
                        ).permitAll()
                        .requestMatchers("/api/v1/faq/**").permitAll()
                        .requestMatchers("/api/v1/sensitive-word/**").permitAll()
                        .requestMatchers("/api/v1/payment/callback/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("R002", "R003", "R004")
                        .requestMatchers("/api/v1/super-admin/**").hasRole("R002")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 密码编码器
     *
     * @return BCrypt密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
