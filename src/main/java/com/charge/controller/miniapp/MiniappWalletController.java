package com.charge.controller.miniapp;

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
 * 小程序端：钱包接口（复用已有钱包服务）
 */
@RestController
@RequestMapping("/api/miniapp/wallet")
public class MiniappWalletController {

    private final WalletService walletService;
    private final MemberRechargeService rechargeService;

    public MiniappWalletController(WalletService walletService,
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
     * 支付成功回调（模拟）
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
