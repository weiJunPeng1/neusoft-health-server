package com.neusoft.health.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.health.modules.system.entity.SmsCode;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

/**
 * 短信验证码Mapper
 * <p>
 * 提供短信验证码数据访问功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Mapper
public interface SmsCodeMapper extends BaseMapper<SmsCode> {

    /**
     * 统计指定时间之后的验证码发送数量
     *
     * @param phoneHash 手机号哈希
     * @param since     开始时间
     * @return 数量
     */
    default long countRecentByPhone(String phoneHash, LocalDateTime since) {
        return selectCount(new LambdaQueryWrapper<SmsCode>()
                .eq(SmsCode::getPhoneHash, phoneHash)
                .ge(SmsCode::getCreatedTime, since));
    }

    /**
     * 查找最新的未使用的验证码
     *
     * @param phoneHash 手机号哈希
     * @return 验证码对象
     */
    default SmsCode findLatestUnusedByPhone(String phoneHash) {
        return selectOne(new LambdaQueryWrapper<SmsCode>()
                .eq(SmsCode::getPhoneHash, phoneHash)
                .eq(SmsCode::getUsed, 0)
                .orderByDesc(SmsCode::getCreatedTime)
                .last("LIMIT 1"));
    }

    /**
     * 统计今日验证码发送数量
     *
     * @param phoneHash 手机号哈希
     * @param since     开始时间
     * @return 数量
     */
    default long countTodayByPhone(String phoneHash, LocalDateTime since) {
        return selectCount(new LambdaQueryWrapper<SmsCode>()
                .eq(SmsCode::getPhoneHash, phoneHash)
                .ge(SmsCode::getCreatedTime, since));
    }
}
