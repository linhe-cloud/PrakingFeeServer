package com.charge.mapper;

import com.charge.entity.Member;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberMapper {

    int insert(Member member);

    int updateById(Member member);

    int deleteById(@Param("id") Long id);

    Member selectById(@Param("id") Long id);

    Member selectByUserId(@Param("userId") Long userId);

    List<Member> selectList(@Param("status") Integer status);
}
