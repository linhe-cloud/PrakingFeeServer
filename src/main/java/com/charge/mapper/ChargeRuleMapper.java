package com.charge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import com.charge.entity.ChargeRule;

@Mapper
public interface ChargeRuleMapper {
    /**
     *  查询某车场当前有效的规则列表（按优先级倒序）
     */
    List<ChargeRule> selectValidRulesByParkingIot(
        @Param("parkingIotId") Long parkingIotId,
        @Param("currentDate") LocalDate currentDate
    );

    ChargeRule selectById(@Param("id") Long id);

    int insert (ChargeRule rule);

    int updateById(ChargeRule rule);

    /**
     * 查询某车场的所有规则(不限制生效时间/状态),用于管理端查看
     */
    List<ChargeRule> selectByParkingIot(@Param("parkingIotId") Long parkingIotId);
}