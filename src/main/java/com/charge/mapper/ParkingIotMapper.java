package com.charge.mapper;

import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;
import com.charge.entity.ParkingIot;


@Mapper
public interface ParkingIotMapper {

    ParkingIot selectById(@Param("id") Long id);
    ParkingIot selectByCode(@Param("code") String code);

}
