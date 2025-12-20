package com.charge.controller.admin;

import com.charge.entity.Member;
import com.charge.service.MemberService;
import com.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员管理接口（管理端）
 */
@RestController
@RequestMapping("/api/admin/member")
public class MemberAdminController {

    private final MemberService memberService;

    public MemberAdminController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 创建会员
     */
    @PostMapping("/create")
    public Result<Member> createMember(@RequestBody Member member) {
        Member created = memberService.createMember(member);
        return Result.success(created);
    }

    /**
     * 更新会员信息
     */
    @PostMapping("/update")
    public Result<Void> updateMember(@RequestBody Member member) {
        memberService.updateMember(member);
        return Result.success();
    }

    /**
     * 删除会员
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteMember(@PathVariable("id") Long id) {
        memberService.deleteMember(id);
        return Result.success();
    }

    /**
     * 根据会员ID查询详情
     */
    @GetMapping("/{id}")
    public Result<Member> getMemberById(@PathVariable("id") Long id) {
        Member member = memberService.getById(id);
        return Result.success(member);
    }

    /**
     * 根据用户ID查询会员信息
     */
    @GetMapping("/user/{userId}")
    public Result<Member> getMemberByUserId(@PathVariable("userId") Long userId) {
        Member member = memberService.getByUserId(userId);
        return Result.success(member);
    }

    /**
     * 查询会员列表
     * status: 1-有效，0-失效，null-全部
     */
    @GetMapping("/list")
    public Result<List<Member>> listMembers(
            @RequestParam(value = "status", required = false) Integer status) {
        List<Member> list = memberService.listMembers(status);
        return Result.success(list);
    }
}
