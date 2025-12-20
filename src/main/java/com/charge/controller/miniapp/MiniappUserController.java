package com.charge.controller.miniapp;

import com.charge.entity.Member;
import com.charge.entity.Wallet;
import com.charge.service.MemberService;
import com.charge.service.WalletService;
import com.common.exception.BusinessException;
import com.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 小程序端：用户信息接口
 */
@RestController
@RequestMapping("/api/miniapp/user")
public class MiniappUserController {

    private final MemberService memberService;
    private final WalletService walletService;

    public MiniappUserController(MemberService memberService, WalletService walletService) {
        this.memberService = memberService;
        this.walletService = walletService;
    }

    /**
     * 获取用户信息（包含会员、钱包）
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestParam("userId") Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 查询会员信息
        Member member = memberService.getByUserId(userId);

        // 查询钱包
        Wallet wallet = walletService.getOrCreateWallet(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("member", member);
        data.put("wallet", wallet);

        return Result.success(data);
    }

    /**
     * 获取会员信息
     */
    @GetMapping("/member")
    public Result<Member> getMemberInfo(@RequestParam("userId") Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        Member member = memberService.getByUserId(userId);
        return Result.success(member);
    }
}
