package com.neusoft.health.framework.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT令牌提供者
 * <p>
 * 负责生成、解析和验证JWT令牌，包括访问令牌和刷新令牌。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Component
public final class JwtTokenProvider {

    /**
     * 签名密钥
     */
    private final SecretKey secretKey;
    /**
     * 访问令牌过期时间
     */
    private final long expiration;
    /**
     * 刷新令牌过期时间
     */
    private final long refreshExpiration;

    /**
     * 构造方法
     *
     * @param secret           签名密钥
     * @param expiration       访问令牌过期时间
     * @param refreshExpiration 刷新令牌过期时间
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
        this.refreshExpiration = refreshExpiration;
    }

    /**
     * 生成访问令牌
     *
     * @param userId   用户ID
     * @param roleCode 角色编码
     * @return JWT令牌
     */
    public String generateToken(Long userId, String roleCode) {
        Date now = new Date();
        return Jwts.builder()
                .claims(Map.of("userId", userId, "roleCode", roleCode))
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 生成刷新令牌
     *
     * @param userId 用户ID
     * @return 刷新令牌
     */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpiration))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析JWT令牌
     *
     * @param token JWT令牌
     * @return 令牌声明
     * @throws ExpiredJwtException 令牌已过期
     * @throws RuntimeException    无效的令牌
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /**
     * 从令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserId(String token) {
        return Long.valueOf(parseToken(token).getSubject());
    }

    /**
     * 检查令牌是否已过期
     *
     * @param token JWT令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            parseToken(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
