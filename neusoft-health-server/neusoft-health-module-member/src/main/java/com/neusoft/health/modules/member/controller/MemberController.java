package com.neusoft.health.modules.member.controller;

import com.neusoft.health.common.result.R;
import com.neusoft.health.framework.util.SecurityUtil;
import com.neusoft.health.modules.member.service.InviteService;
import com.neusoft.health.modules.member.service.MemberService;
import com.neusoft.health.modules.member.vo.InviteInfoVO;
import com.neusoft.health.modules.member.vo.MemberLevelVO;
import com.neusoft.health.modules.member.vo.MemberStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "会员中心", description = "会员状态查询、邀请码管理接口")
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final InviteService inviteService;

    @Operation(summary = "获取会员状态")
    @GetMapping("/status")
    public R<MemberStatusVO> getStatus() {
        return R.ok(memberService.getMemberStatus(SecurityUtil.requireCurrentUserId()));
    }

    @Operation(summary = "获取所有会员等级权益说明")
    @GetMapping("/levels")
    public R<List<MemberLevelVO>> getMemberLevels() {
        List<MemberLevelVO> levels = new ArrayList<>();
        
        MemberLevelVO l0 = new MemberLevelVO();
        l0.setLevelCode("L0");
        l0.setLevelName("普通用户");
        l0.setDailyQuota(3);
        l0.setContextRounds(5);
        l0.setAutoSync(false);
        l0.setDeepAnalysis(false);
        l0.setExportEnabled(false);
        l0.setDescription("基础咨询服务，每天3次免费咨询");
        levels.add(l0);

        MemberLevelVO l1 = new MemberLevelVO();
        l1.setLevelCode("L1");
        l1.setLevelName("白银会员");
        l1.setDailyQuota(20);
        l1.setContextRounds(15);
        l1.setAutoSync(true);
        l1.setDeepAnalysis(false);
        l1.setExportEnabled(true);
        l1.setDescription("畅享更多咨询次数，支持咨询记录导出");
        levels.add(l1);

        MemberLevelVO l2 = new MemberLevelVO();
        l2.setLevelCode("L2");
        l2.setLevelName("黄金会员");
        l2.setDailyQuota(50);
        l2.setContextRounds(30);
        l2.setAutoSync(true);
        l2.setDeepAnalysis(false);
        l2.setExportEnabled(true);
        l2.setDescription("更高频次的专业咨询服务");
        levels.add(l2);

        MemberLevelVO l3 = new MemberLevelVO();
        l3.setLevelCode("L3");
        l3.setLevelName("铂金会员");
        l3.setDailyQuota(0);
        l3.setContextRounds(50);
        l3.setAutoSync(true);
        l3.setDeepAnalysis(true);
        l3.setExportEnabled(true);
        l3.setDescription("无限次咨询，深度健康分析，家庭共享权益");
        levels.add(l3);

        return R.ok(levels);
    }

    @Operation(summary = "获取邀请信息")
    @GetMapping("/invite")
    public R<InviteInfoVO> getInviteInfo() {
        return R.ok(inviteService.getInviteInfo(SecurityUtil.requireCurrentUserId()));
    }

    @Operation(summary = "获取会员历史")
    @GetMapping("/history")
    public R<?> getHistory() {
        return R.ok(memberService.getMembershipHistory(SecurityUtil.requireCurrentUserId()));
    }
}