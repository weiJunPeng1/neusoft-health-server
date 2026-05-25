package com.neusoft.health.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.health.modules.admin.service.AdminUserService;
import com.neusoft.health.common.entity.User;
import com.neusoft.health.common.mapper.UserMapper;
import com.neusoft.health.modules.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;

    @Override
    public List<UserVO> listAllUsers(String keyword, int page, int size) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .orderByDesc(User::getCreatedTime);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                    .like(User::getNickname, keyword)
                    .or().like(User::getPhoneHash, keyword)
                    .or().like(User::getNickname, keyword));
        }
        List<User> users = userMapper.selectList(wrapper);
        return users.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setStatus(status);
            userMapper.updateById(user);
        }
    }

    @Override
    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }

    @Override
    public UserVO getUserDetail(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null ? toVO(user) : null;
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}

