package com.neusoft.health.modules.system.service.impl;

import com.neusoft.health.common.exception.BusinessException;
import com.neusoft.health.common.result.ResultCode;
import com.neusoft.health.common.utils.PhoneUtil;
import com.neusoft.health.framework.config.SmsProperties;
import com.neusoft.health.framework.util.SmsUtil;
import com.neusoft.health.modules.system.dto.SmsCodeSendDTO;
import com.neusoft.health.modules.system.entity.SmsCode;
import com.neusoft.health.modules.system.mapper.SmsCodeMapper;
import com.neusoft.health.modules.system.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务实现
 * <p>
 * 提供短信验证码发送和验证功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    /**
     * 发送间隔限制Redis key前缀
     */
    private static final String SMS_INTERVAL_KEY_PREFIX = "sms:interval:";
    /**
     * 每日发送次数Redis key前缀
     */
    private static final String SMS_DAILY_KEY_PREFIX = "sms:daily:";
    /**
     * 验证码Redis key前缀
     */
    private static final String SMS_CODE_KEY_PREFIX = "sms:code:";

    private final SmsProperties smsProperties;
    private final SmsUtil smsUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SmsCodeMapper smsCodeMapper;

    @Override
    @Transactional
    public void sendVerificationCode(SmsCodeSendDTO dto, String clientIp) {
        String phoneHash = PhoneUtil.hash(dto.getPhone());

        String intervalKey = SMS_INTERVAL_KEY_PREFIX + phoneHash;
        String dailyKey = SMS_DAILY_KEY_PREFIX + phoneHash;

        Boolean intervalBlocked = redisTemplate.hasKey(intervalKey);
        if (Boolean.TRUE.equals(intervalBlocked)) {
            throw new BusinessException(ResultCode.SMS_CODE_FREQUENT);
        }

        Object dailyCountObj = redisTemplate.opsForValue().get(dailyKey);
        int dailyCount = dailyCountObj != null ? (Integer) dailyCountObj : 0;
        if (dailyCount >= smsProperties.getDailyMaxCount()) {
            throw new BusinessException(ResultCode.SMS_CODE_DAILY_LIMIT);
        }

        String code = String.format("%06d", (int) (Math.random() * 1000000));

        boolean sent = smsUtil.send(dto.getPhone(), code);
        if (!sent) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR.getCode(), "短信发送失败，请稍后再试");
        }

        SmsCode smsCode = new SmsCode();
        smsCode.setPhoneHash(phoneHash);
        smsCode.setCode(code);
        smsCode.setExpireTime(LocalDateTime.now().plusMinutes(5));
        smsCode.setUsed(0);
        smsCode.setSendIp(clientIp);
        smsCodeMapper.insert(smsCode);

        redisTemplate.opsForValue().set(intervalKey, 1, smsProperties.getMinIntervalSeconds(), TimeUnit.SECONDS);
        redisTemplate.opsForValue().increment(dailyKey);
        redisTemplate.expire(dailyKey, getSecondsUntilMidnight(), TimeUnit.SECONDS);

        String codeKey = SMS_CODE_KEY_PREFIX + phoneHash;
        redisTemplate.opsForValue().set(codeKey, code, 5, TimeUnit.MINUTES);

        log.info("短信验证码发送成功, phoneHash: {}", phoneHash);
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        String phoneHash = PhoneUtil.hash(phone);
        String codeKey = SMS_CODE_KEY_PREFIX + phoneHash;

        Object cachedCode = redisTemplate.opsForValue().get(codeKey);
        if (cachedCode != null && cachedCode.equals(code)) {
            redisTemplate.delete(codeKey);
            return true;
        }

        SmsCode latestCode = smsCodeMapper.findLatestUnusedByPhone(phoneHash);
        if (latestCode == null || !latestCode.getCode().equals(code)) {
            return false;
        }
        if (latestCode.getExpireTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        latestCode.setUsed(1);
        smsCodeMapper.updateById(latestCode);
        return true;
    }

    /**
     * 获取当前时间到第二天凌晨的秒数
     *
     * @return 秒数
     */
    private long getSecondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return ChronoUnit.SECONDS.between(now, midnight);
    }
}
