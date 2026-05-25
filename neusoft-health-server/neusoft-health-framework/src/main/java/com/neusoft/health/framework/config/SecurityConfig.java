package com.neusoft.health.framework.config;

import com.neusoft.health.framework.security.JwtAccessDeniedHandler;
import com.neusoft.health.framework.security.JwtAuthenticationFilter;
import com.neusoft.health.framework.security.SecurityAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
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
                        .requestMatchers("/api/v1/health-search/**").permitAll()
                        .requestMatchers("/api/v1/sensitive-word/**").permitAll()
                        .requestMatchers("/api/v1/payment/callback/**").permitAll()
                        .requestMatchers("/api/v1/payment/alipay-pay-page/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("R002", "R003", "R004")
                        .requestMatchers("/api/v1/super-admin/**").hasRole("R002")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * CORS配置源
     *
     * @return CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许所有来源
        config.addAllowedOriginPattern("*");
        
        // 允许携带凭证
        config.setAllowCredentials(true);
        
        // 允许所有请求方法
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // 允许所有请求头
        config.addAllowedHeader("*");
        
        // 暴露的响应头
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Disposition");
        
        // 预检请求缓存时间
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return source;
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