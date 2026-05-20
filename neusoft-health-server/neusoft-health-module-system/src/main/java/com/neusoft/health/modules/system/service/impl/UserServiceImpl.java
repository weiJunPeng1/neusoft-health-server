package com.neusoft.health.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.common.enums.UserStatusEnum;
import com.neusoft.health.common.exception.BusinessException;
import com.neusoft.health.common.result.PageResult;
import com.neusoft.health.common.result.ResultCode;
import com.neusoft.health.common.utils.AesUtil;
import com.neusoft.health.common.utils.PhoneUtil;
import com.neusoft.health.modules.system.dto.UserPageQueryDTO;
import com.neusoft.health.modules.system.dto.UserStatusDTO;
import com.neusoft.health.modules.system.dto.UserUpdateDTO;
import com.neusoft.health.modules.system.entity.User;
import com.neusoft.health.modules.system.mapper.UserMapper;
import com.neusoft.health.modules.system.service.UserService;
import com.neusoft.health.modules.system.vo.UserDetailVO;
import com.neusoft.health.modules.system.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 * <p>
 * 提供用户信息查询、修改、状态管理等功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public UserVO getProfile(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return toVO(user);
    }

    @Override
    public UserDetailVO getDetailById(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        UserDetailVO vo = new UserDetailVO();
        vo.setId(user.getId());
        vo.setPhone(PhoneUtil.mask(AesUtil.decrypt(user.getPhoneEncrypted())));
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setGender(user.getGender());
        vo.setAge(user.getAge());
        vo.setLoginType(user.getLoginType());
        vo.setDisclaimerAccepted(user.getDisclaimerAccepted());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setStatus(user.getStatus());
        vo.setCreatedTime(user.getCreatedTime());
        return vo;
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UserUpdateDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setNickname(dto.getNickname());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setGender(dto.getGender());
        user.setAge(dto.getAge());
        updateById(user);
    }

    @Override
    public PageResult<UserVO> pageUsers(UserPageQueryDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .like(StringUtils.hasText(dto.getNickname()), User::getNickname, dto.getNickname())
                .eq(dto.getStatus() != null, User::getStatus, dto.getStatus())
                .orderByDesc(User::getCreatedTime);

        IPage<User> page = page(new Page<>(dto.getPage(), dto.getPageSize()), wrapper);

        List<UserVO> list = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(page.getTotal(), dto.getPage(), dto.getPageSize(), list);
    }

    @Override
    @Transactional
    public void toggleStatus(UserStatusDTO dto) {
        User user = getById(dto.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        int newStatus = Objects.equals(UserStatusEnum.NORMAL.getCode(), user.getStatus()) ? UserStatusEnum.DISABLED.getCode() : UserStatusEnum.NORMAL.getCode();
        user.setStatus(newStatus);
        updateById(user);
    }

    /**
     * 将用户实体转换为VO
     *
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setPhone(PhoneUtil.mask(AesUtil.decrypt(user.getPhoneEncrypted())));
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setGender(user.getGender());
        vo.setAge(user.getAge());
        vo.setLoginType(user.getLoginType());
        vo.setDisclaimerAccepted(user.getDisclaimerAccepted());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setStatus(user.getStatus());
        vo.setCreatedTime(user.getCreatedTime());
        return vo;
    }
}

