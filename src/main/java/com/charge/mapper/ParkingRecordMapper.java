package com.charge.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.charge.entity.ParkingRecord;


@Mapper
public interface ParkingRecordMapper {

    ParkingRecord selectById(Long id);
    int updateById(ParkingRecord parkingRecord);
    int insert(ParkingRecord parkingRecord);
}
