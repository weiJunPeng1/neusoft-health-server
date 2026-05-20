package com.neusoft.health.modules.system.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.framework.util.SecurityUtil;
import com.neusoft.health.modules.system.dto.UserUpdateDTO;
import com.neusoft.health.modules.system.service.UserService;
import com.neusoft.health.modules.system.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息控制器
 * <p>
 * 提供当前用户个人信息查询与修改接口
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Tag(name = "用户信息", description = "当前用户个人信息查询与修改接口")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取个人信息
     *
     * @return 用户信息
     */
    @Operation(summary = "获取个人信息", description = "获取当前登录用户的详细信息，包含昵称、头像、手机号等")
    @GetMapping("/profile")
    public R<UserVO> getProfile() {
        return R.ok(userService.getProfile(SecurityUtil.requireCurrentUserId()));
    }

    /**
     * 更新个人信息
     *
     * @param dto 更新信息请求
     * @return 操作结果
     */
    @Operation(summary = "更新个人信息", description = "修改当前登录用户的昵称、头像、性别、生日等基本信息")
    @PutMapping("/profile")
    public R<Void> updateProfile(@Valid @RequestBody UserUpdateDTO dto) {
        userService.updateProfile(SecurityUtil.requireCurrentUserId(), dto);
        return R.ok();
    }
}
