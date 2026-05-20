package com.neusoft.health.modules.system.service;

import com.neusoft.health.modules.system.dto.*;
import com.neusoft.health.modules.system.vo.LoginVO;
import com.neusoft.health.modules.system.vo.TokenRefreshVO;

/**
 * 认证服务
 * <p>
 * 提供用户登录、密码管理、令牌刷新等认证相关功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface AuthService {

    /**
     * 短信验证码登录
     *
     * @param dto        短信验证码登录请求
     * @param clientIp   客户端IP
     * @param userAgent  用户代理信息
     * @return 登录结果，包含JWT令牌和用户信息
     */
    LoginVO login(SmsCodeLoginDTO dto, String clientIp, String userAgent);

    /**
     * 密码登录
     *
     * @param dto        密码登录请求
     * @param clientIp   客户端IP
     * @param userAgent  用户代理信息
     * @return 登录结果，包含JWT令牌和用户信息
     */
    LoginVO passwordLogin(PasswordLoginDTO dto, String clientIp, String userAgent);

    /**
     * 刷新JWT令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的令牌
     */
    TokenRefreshVO refreshToken(String refreshToken);

    /**
     * 退出登录
     *
     * @param userId 用户ID
     */
    void logout(Long userId);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param dto    修改密码请求
     */
    void updatePassword(Long userId, UserPasswordDTO dto);
}
