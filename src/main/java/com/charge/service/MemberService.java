package com.charge.service;

import com.charge.entity.Member;

import java.util.List;

public interface MemberService {

    /**
     * 创建会员
     */
    Member createMember(Member member);

    /**
     * 更新会员信息
     */
    void updateMember(Member member);

    /**
     * 删除会员
     */
    void deleteMember(Long id);

    /**
     * 根据会员ID查询
     */
    Member getById(Long id);

    /**
     * 根据用户ID查询会员
     */
    Member getByUserId(Long userId);

    /**
     * 查询会员列表（可按状态筛选）
     * status 为 null 时查询全部
     */
    List<Member> listMembers(Integer status);
}
