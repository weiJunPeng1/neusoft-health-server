package com.neusoft.health.modules.user.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.framework.util.SecurityUtil;
import com.neusoft.health.modules.user.dto.HealthProfileUpdateDTO;
import com.neusoft.health.modules.user.dto.UserSettingUpdateDTO;
import com.neusoft.health.modules.user.service.UserFavoriteService;
import com.neusoft.health.modules.user.service.UserHealthProfileService;
import com.neusoft.health.modules.user.service.UserSettingService;
import com.neusoft.health.modules.user.vo.UserFavoriteVO;
import com.neusoft.health.modules.user.vo.UserHealthProfileVO;
import com.neusoft.health.modules.user.vo.UserSettingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户个性化", description = "用户健康档案、偏好设置和收藏管理等个人数据接口")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserHealthProfileService healthProfileService;
    private final UserSettingService settingService;
    private final UserFavoriteService favoriteService;

    @Operation(summary = "获取健康档案", description = "获取当前用户的身高、体重、血型、过敏史、既往病史等健康档案信息")
    @GetMapping("/health-profile")
    public R<UserHealthProfileVO> getHealthProfile() {
        return R.ok(healthProfileService.getProfile(SecurityUtil.requireCurrentUserId()));
    }

    @Operation(summary = "更新健康档案", description = "更新当前用户的健康档案信息，未传字段保持不变")
    @PutMapping("/health-profile")
    public R<Void> updateHealthProfile(@Valid @RequestBody HealthProfileUpdateDTO dto) {
        healthProfileService.updateProfile(SecurityUtil.requireCurrentUserId(), dto);
        return R.ok();
    }

    @Operation(summary = "获取用户设置", description = "获取当前用户的通知偏好、语音速度、隐私设置等个性化配置")
    @GetMapping("/settings")
    public R<UserSettingVO> getSettings() {
        return R.ok(settingService.getSettings(SecurityUtil.requireCurrentUserId()));
    }

    @Operation(summary = "更新用户设置", description = "更新当前用户的个性化设置，未传字段保持不变")
    @PutMapping("/settings")
    public R<Void> updateSettings(@Valid @RequestBody UserSettingUpdateDTO dto) {
        settingService.updateSettings(SecurityUtil.requireCurrentUserId(), dto);
        return R.ok();
    }

    @Operation(summary = "切换收藏状态", description = "切换指定消息的收藏/取消收藏状态，返回当前收藏状态")
    @PostMapping("/favorite/toggle")
    public R<Boolean> toggleFavorite(@RequestBody UserFavoriteVO dto) {
        return R.ok(favoriteService.toggleFavorite(SecurityUtil.requireCurrentUserId(), dto.getMessageId()));
    }

    @Operation(summary = "获取收藏列表", description = "获取当前用户收藏的所有健康咨询消息列表")
    @GetMapping("/favorites")
    public R<List<UserFavoriteVO>> listFavorites() {
        return R.ok(favoriteService.listFavorites(SecurityUtil.requireCurrentUserId()));
    }

    @Operation(summary = "检查是否已收藏", description = "检查指定消息是否已被当前用户收藏")
    @GetMapping("/favorite/check")
    public R<Boolean> checkFavorite(
            @Parameter(description = "消息ID") @RequestParam Long messageId) {
        return R.ok(favoriteService.checkFavorite(SecurityUtil.requireCurrentUserId(), messageId));
    }
}
