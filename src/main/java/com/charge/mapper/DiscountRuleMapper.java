package com.charge.mapper;

import com.charge.entity.DiscountRule;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DiscountRuleMapper {

    int insert(DiscountRule rule);

    int updateById(DiscountRule rule);

    DiscountRule selectById(@Param("id") Long id);

    int deleteById(@Param("id") Long id);

    /**
     * 按状态查询列表
     * status 为 null 时查询全部
     */
    List<DiscountRule> selectList(@Param("status") Integer status);

    /**
     * 按规则编码查询当前有效的优惠规则
     */
    DiscountRule selectEffectiveByCode(@Param("ruleCode") String ruleCode,
                                       @Param("now") LocalDateTime now);
}
