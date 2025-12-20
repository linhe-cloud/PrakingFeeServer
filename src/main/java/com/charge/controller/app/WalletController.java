package com.charge.controller.app;

import com.charge.entity.MemberRecharge;
import com.charge.entity.Wallet;
import com.charge.service.MemberRechargeService;
import com.charge.service.WalletService;
import com.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 钱包和充值接口（APP端）
 */
@RestController
@RequestMapping("/api/app/wallet")
public class WalletController {

    private final WalletService walletService;
    private final MemberRechargeService rechargeService;

    public WalletController(WalletService walletService,
                           MemberRechargeService rechargeService) {
        this.walletService = walletService;
        this.rechargeService = rechargeService;
    }

    /**
     * 查询钱包余额
     */
    @GetMapping("/balance")
    public Result<Wallet> getBalance(@RequestParam("userId") Long userId) {
        Wallet wallet = walletService.getOrCreateWallet(userId);
        return Result.success(wallet);
    }

    /**
     * 发起充值（创建充值订单）
     * @param userId 用户ID
     * @param amount 充值金额(分)
     * @return 充值单号（用于调起支付）
     */
    @PostMapping("/recharge/create")
    public Result<Map<String, String>> createRecharge(
            @RequestParam("userId") Long userId,
            @RequestParam("amount") Long amount) {
        
        String rechargeNo = rechargeService.createRechargeOrder(userId, amount);
        
        Map<String, String> data = new HashMap<>();
        data.put("rechargeNo", rechargeNo);
        data.put("message", "充值订单创建成功，请完成支付");
        
        return Result.success(data);
    }

    /**
     * 支付成功回调（模拟，实际应由支付平台回调）
     * @param rechargeNo 充值单号
     * @param payChannel 支付渠道
     */
    @PostMapping("/recharge/paySuccess")
    public Result<Void> paySuccess(
            @RequestParam("rechargeNo") String rechargeNo,
            @RequestParam("payChannel") String payChannel) {
        
        rechargeService.paySuccess(rechargeNo, payChannel);
        return Result.success();
    }

    /**
     * 查询充值记录
     */
    @GetMapping("/recharge/list")
    public Result<List<MemberRecharge>> listRecharge(@RequestParam("userId") Long userId) {
        List<MemberRecharge> list = rechargeService.listByUserId(userId);
        return Result.success(list);
    }

    /**
     * 查询充值订单详情
     */
    @GetMapping("/recharge/detail")
    public Result<MemberRecharge> getRechargeDetail(@RequestParam("rechargeNo") String rechargeNo) {
        MemberRecharge recharge = rechargeService.getByRechargeNo(rechargeNo);
        return Result.success(recharge);
    }
}
