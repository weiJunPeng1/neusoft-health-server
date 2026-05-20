package com.neusoft.health.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.neusoft.health.common.enums.UserStatusEnum;
import com.neusoft.health.common.exception.BusinessException;
import com.neusoft.health.common.result.ResultCode;
import com.neusoft.health.common.utils.AesUtil;
import com.neusoft.health.common.utils.PhoneUtil;
import com.neusoft.health.framework.security.JwtTokenProvider;
import com.neusoft.health.modules.system.dto.PasswordLoginDTO;
import com.neusoft.health.modules.system.dto.SmsCodeLoginDTO;
import com.neusoft.health.modules.system.dto.UserPasswordDTO;
import com.neusoft.health.modules.system.entity.User;
import com.neusoft.health.modules.system.entity.UserLoginLog;
import com.neusoft.health.modules.system.mapper.SmsCodeMapper;
import com.neusoft.health.modules.system.mapper.UserLoginLogMapper;
import com.neusoft.health.modules.system.mapper.UserMapper;
import com.neusoft.health.modules.system.service.AuthService;
import com.neusoft.health.modules.system.service.SmsService;
import com.neusoft.health.modules.system.vo.LoginVO;
import com.neusoft.health.modules.system.vo.TokenRefreshVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * 认证服务实现
 * <p>
 * 提供用户登录、密码管理、令牌刷新等认证相关功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserLoginLogMapper userLoginLogMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;

    @Override
    @Transactional
    public LoginVO login(SmsCodeLoginDTO dto, String clientIp, String userAgent) {
        String phoneHash = PhoneUtil.hash(dto.getPhone());
        
        boolean valid = smsService.verifyCode(dto.getPhone(), dto.getCode());
        if (!valid) {
            throw new BusinessException(ResultCode.SMS_CODE_ERROR);
        }

        User user = userMapper.findByPhoneHash(phoneHash).orElseGet(() -> {
            User newUser = new User();
            newUser.setPhoneHash(phoneHash);
            newUser.setPhoneEncrypted(AesUtil.encrypt(dto.getPhone()));
            newUser.setNickname("用户" + System.currentTimeMillis() % 100000);
            newUser.setLoginType("sms");
            newUser.setDisclaimerAccepted(0);
            newUser.setStatus(UserStatusEnum.NORMAL.getCode());
            userMapper.insert(newUser);
            generateInviteCodeInline(newUser);
            if (dto.getInviteCode() != null && !dto.getInviteCode().isEmpty()) {
                bindInviteRelationInline(newUser, dto.getInviteCode());
            }
            return newUser;
        });

        if (Objects.equals(UserStatusEnum.DISABLED.getCode(), user.getStatus())) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        String roleCode = "R001";
        String accessToken = jwtTokenProvider.generateToken(user.getId(), roleCode);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        UserLoginLog loginLog = new UserLoginLog();
        loginLog.setUserId(user.getId());
        loginLog.setLoginType("sms");
        loginLog.setLoginIp(clientIp);
        loginLog.setUserAgent(userAgent);
        loginLog.setLoginResult(1);
        userLoginLogMapper.insert(loginLog);

        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);
        vo.setExpiresIn(604800L);
        return vo;
    }

    @Override
    @Transactional
    public LoginVO passwordLogin(PasswordLoginDTO dto, String clientIp, String userAgent) {
        String phoneHash = PhoneUtil.hash(dto.getPhone());
        Optional<User> userOpt = userMapper.findByPhoneHash(phoneHash);
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        User user = userOpt.get();
        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "该账号未设置密码，请使用短信验证码登录");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "手机号或密码错误");
        }
        if (Objects.equals(UserStatusEnum.DISABLED.getCode(), user.getStatus())) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        String roleCode = "R001";
        String accessToken = jwtTokenProvider.generateToken(user.getId(), roleCode);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        UserLoginLog loginLog = new UserLoginLog();
        loginLog.setUserId(user.getId());
        loginLog.setLoginType("password");
        loginLog.setLoginIp(clientIp);
        loginLog.setUserAgent(userAgent);
        loginLog.setLoginResult(1);
        userLoginLogMapper.insert(loginLog);

        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);
        vo.setExpiresIn(604800L);
        return vo;
    }

    @Override
    public TokenRefreshVO refreshToken(String refreshToken) {
        try {
            Long userId = jwtTokenProvider.getUserId(refreshToken);
            String roleCode = "R001";
            String newAccessToken = jwtTokenProvider.generateToken(userId, roleCode);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);
            TokenRefreshVO vo = new TokenRefreshVO();
            vo.setAccessToken(newAccessToken);
            vo.setRefreshToken(newRefreshToken);
            vo.setExpiresIn(604800L);
            return vo;
        } catch (Exception e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
    }

    @Override
    public void logout(Long userId) {
        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getLastLoginTime, null));
    }

    @Override
    public void updatePassword(Long userId, UserPasswordDTO dto) {
        String passwordHash = passwordEncoder.encode(dto.getNewPassword());
        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPasswordHash, passwordHash));
    }
    
    /**
     * 生成用户邀请码
     *
     * @param user 用户对象
     */
    private void generateInviteCodeInline(User user) {
        if (user.getInviteCode() != null && !user.getInviteCode().isEmpty()) {
            return;
        }
        String code;
        int attempts = 0;
        do {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            Long existing = userMapper.selectCount(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>().eq(User::getInviteCode, code));
            if (existing == 0) break;
            attempts++;
        } while (attempts < 10);
        if (attempts >= 10) {
            throw new BusinessException(ResultCode.INVITE_CODE_FAILED);
        }
        user.setInviteCode(code);
        userMapper.updateById(user);
    }

    /**
     * 绑定邀请关系
     *
     * @param newUser     新用户
     * @param inviteCode  邀请码
     */
    private void bindInviteRelationInline(User newUser, String inviteCode) {
        if (newUser.getInvitedBy() != null) {
            return;
        }
        User inviter = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>().eq(User::getInviteCode, inviteCode));
        if (inviter == null || inviter.getId().equals(newUser.getId())) {
            return;
        }
        newUser.setInvitedBy(inviter.getId());
        userMapper.updateById(newUser);
    }
}
