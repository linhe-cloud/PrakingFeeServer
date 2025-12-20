package com.charge.controller.admin;

import com.charge.entity.MemberRecharge;
import com.charge.service.MemberRechargeService;
import com.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 充值记录管理接口（管理端）
 */
@RestController
@RequestMapping("/api/admin/recharge")
public class RechargeAdminController {

    private final MemberRechargeService rechargeService;

    public RechargeAdminController(MemberRechargeService rechargeService) {
        this.rechargeService = rechargeService;
    }

    /**
     * 查询用户充值记录
     */
    @GetMapping("/list")
    public Result<List<MemberRecharge>> listRecharge(@RequestParam("userId") Long userId) {
        List<MemberRecharge> list = rechargeService.listByUserId(userId);
        return Result.success(list);
    }

    /**
     * 查询充值订单详情
     */
    @GetMapping("/detail")
    public Result<MemberRecharge> getDetail(@RequestParam("rechargeNo") String rechargeNo) {
        MemberRecharge recharge = rechargeService.getByRechargeNo(rechargeNo);
        return Result.success(recharge);
    }
}
