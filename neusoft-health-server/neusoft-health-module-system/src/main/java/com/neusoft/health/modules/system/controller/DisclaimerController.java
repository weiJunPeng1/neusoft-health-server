package com.neusoft.health.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.neusoft.health.common.result.R;
import com.neusoft.health.framework.util.SecurityUtil;
import com.neusoft.health.common.entity.User;
import com.neusoft.health.common.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 免责声明控制器
 * <p>
 * 提供用户免责声明确认相关接口
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Tag(name = "免责声明", description = "用户免责声明确认相关接口")
@RestController
@RequestMapping("/api/v1/disclaimer")
@RequiredArgsConstructor
public class DisclaimerController {

    private final UserMapper userMapper;

    /**
     * 同意免责声明
     *
     * @return 操作结果
     */
    @Operation(summary = "同意免责声明", description = "当前用户确认同意免责声明，记录用户同意状态")
    @PostMapping("/accept")
    public R<Void> accept() {
        Long userId = SecurityUtil.requireCurrentUserId();
        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getDisclaimerAccepted, 1));
        return R.ok();
    }
}
