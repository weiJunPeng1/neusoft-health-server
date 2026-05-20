package com.neusoft.health.modules.system.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.framework.util.SecurityUtil;
import com.neusoft.health.modules.system.dto.*;
import com.neusoft.health.modules.system.service.AuthService;
import com.neusoft.health.modules.system.service.SmsService;
import com.neusoft.health.modules.system.vo.LoginVO;
import com.neusoft.health.modules.system.vo.TokenRefreshVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * <p>
 * 提供用户注册、登录、密码管理、令牌刷新等认证相关接口
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Tag(name = "认证管理", description = "用户注册、登录、密码管理、令牌刷新等认证相关接口")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SmsService  smsService;

    /**
     * 发送短信验证码
     *
     * @param dto     发送验证码请求
     * @param request HTTP请求
     * @return 操作结果
     */
    @Operation(summary = "发送短信验证码", description = "向指定手机号发送登录或重置密码的短信验证码，60秒内不可重复发送")
    @PostMapping({"/send-code", "/sms-code/send"})
    public R<Void> sendCode(@Valid @RequestBody SmsCodeSendDTO dto, HttpServletRequest request) {
        smsService.sendVerificationCode(dto, request.getRemoteAddr());
        return R.ok();
    }

    /**
     * 短信验证码登录
     *
     * @param dto     短信验证码登录请求
     * @param request HTTP请求
     * @return 登录结果
     */
    @Operation(summary = "短信验证码登录", description = "使用手机号和短信验证码进行登录，登录成功返回JWT令牌")
    @PostMapping({"/login", "/sms-code/verify"})
    public R<LoginVO> smsLogin(@Valid @RequestBody SmsCodeLoginDTO dto, HttpServletRequest request) {
        LoginVO vo = authService.login(dto, request.getRemoteAddr(), request.getHeader("User-Agent"));
        return R.ok(vo);
    }

    /**
     * 密码登录
     *
     * @param dto     密码登录请求
     * @param request HTTP请求
     * @return 登录结果
     */
    @Operation(summary = "密码登录", description = "使用手机号和密码进行登录，登录成功返回JWT令牌")
    @PostMapping("/login/password")
    public R<LoginVO> passwordLogin(@Valid @RequestBody PasswordLoginDTO dto, HttpServletRequest request) {
        LoginVO vo = authService.passwordLogin(dto, request.getRemoteAddr(), request.getHeader("User-Agent"));
        return R.ok(vo);
    }

    /**
     * 退出登录
     *
     * @return 操作结果
     */
    @Operation(summary = "退出登录", description = "使当前用户的JWT令牌失效，需在请求头中携带Authorization")
    @PostMapping("/logout")
    public R<Void> logout() {
        authService.logout(SecurityUtil.requireCurrentUserId());
        return R.ok();
    }

    /**
     * 修改密码
     *
     * @param dto 修改密码请求
     * @return 操作结果
     */
    @Operation(summary = "修改密码", description = "当前用户修改登录密码，需要提供旧密码和新密码")
    @PostMapping("/password")
    public R<Void> updatePassword(@Valid @RequestBody UserPasswordDTO dto) {
        authService.updatePassword(SecurityUtil.requireCurrentUserId(), dto);
        return R.ok();
    }

    /**
     * 刷新令牌
     *
     * @param bearerToken Bearer格式的刷新令牌
     * @return 新的令牌
     */
    @Operation(summary = "刷新令牌", description = "使用refreshToken刷新JWT令牌，获取新的accessToken")
    @PostMapping("/refresh")
    public R<TokenRefreshVO> refreshToken(
            @Parameter(description = "Bearer格式的刷新令牌", required = true)
            @RequestHeader("Authorization") String bearerToken) {
        String refreshToken = bearerToken.substring(7);
        TokenRefreshVO vo = authService.refreshToken(refreshToken);
        return R.ok(vo);
    }
}
