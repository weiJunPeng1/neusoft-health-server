package com.neusoft.health.modules.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.health.modules.member.dto.AdminGrantDTO;
import com.neusoft.health.modules.member.entity.UserMembership;
import com.neusoft.health.modules.member.vo.MemberStatusVO;
import java.util.List;

/**
 * 会员服务
 * <p>
 * 提供会员状态查询、会员权益授予、激活等功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public interface MemberService extends IService<UserMembership> {

    /**
     * 获取会员状态
     *
     * @param userId 用户ID
     * @return 会员状态VO
     */
    MemberStatusVO getMemberStatus(Long userId);

    /**
     * 管理员授予会员
     *
     * @param dto 授予DTO
     */
    void grantMembership(AdminGrantDTO dto);

    /**
     * 撤销会员
     *
     * @param userId 用户ID
     */
    void revokeMembership(Long userId);

    /**
     * 激活会员
     *
     * @param userId 用户ID
     * @param levelCode 会员等级编码
     * @param durationDays 有效天数
     * @param orderId 订单ID
     */
    void activateMembership(Long userId, String levelCode, int durationDays, Long orderId);

    /**
     * 获取会员历史记录
     *
     * @param userId 用户ID
     * @return 会员历史记录列表
     */
    List<UserMembership> getMembershipHistory(Long userId);
}