package com.charge.service.impl;

import com.charge.entity.Member;
import com.charge.mapper.MemberMapper;
import com.charge.service.MemberService;
import com.charge.service.ChargeCacheService;
import com.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final ChargeCacheService chargeCacheService;

    public MemberServiceImpl(MemberMapper memberMapper, 
                            ChargeCacheService chargeCacheService) {
        this.memberMapper = memberMapper;
        this.chargeCacheService = chargeCacheService;
    }

    @Override
    public Member createMember(Member member) {
        if (member.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        // 检查该用户是否已经是会员
        Member existing = memberMapper.selectByUserId(member.getUserId());
        if (existing != null) {
            throw new BusinessException("该用户已是会员，userId=" + member.getUserId());
        }
        // 设置默认值
        if (member.getLevel() == null) {
            member.setLevel(0); // 默认普通会员
        }
        if (member.getStatus() == null) {
            member.setStatus(1); // 默认有效
        }
        memberMapper.insert(member);
        
        // 写入缓存
        chargeCacheService.cacheMember(member.getUserId(), member);
        log.info("创建会员成功并缓存: userId={}, memberId={}", member.getUserId(), member.getId());
        
        return member;
    }

    @Override
    public void updateMember(Member member) {
        if (member.getId() == null) {
            throw new BusinessException("会员ID不能为空");
        }
        // 先查询原会员信息，获取userId
        Member oldMember = memberMapper.selectById(member.getId());
        if (oldMember == null) {
            throw new BusinessException("会员不存在，id=" + member.getId());
        }
        
        int rows = memberMapper.updateById(member);
        if (rows == 0) {
            throw new BusinessException("会员更新失败，id=" + member.getId());
        }
        
        // 删除缓存，下次查询时重新加载
        chargeCacheService.evictMember(oldMember.getUserId());
        log.info("更新会员信息并清除缓存: userId={}, memberId={}", oldMember.getUserId(), member.getId());
    }

    @Override
    public void deleteMember(Long id) {
        if (id == null) {
            throw new BusinessException("会员ID不能为空");
        }
        // 先查询会员信息，获取userId
        Member member = memberMapper.selectById(id);
        if (member != null) {
            // 删除数据库记录
            memberMapper.deleteById(id);
            // 删除缓存
            chargeCacheService.evictMember(member.getUserId());
            log.info("删除会员并清除缓存: userId={}, memberId={}", member.getUserId(), id);
        }
    }

    @Override
    public Member getById(Long id) {
        if (id == null) {
            throw new BusinessException("会员ID不能为空");
        }
        Member member = memberMapper.selectById(id);
        if (member == null) {
            throw new BusinessException("会员不存在，id=" + id);
        }
        return member;
    }

    @Override
    public Member getByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        return memberMapper.selectByUserId(userId);
    }

    @Override
    public List<Member> listMembers(Integer status) {
        return memberMapper.selectList(status);
    }
}
