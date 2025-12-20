package com.parking.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.parking.entity.ParkingRecord;



@Mapper
public interface ParkingRecordMapper {

    ParkingRecord selectById(Long id);

    int updateById(ParkingRecord parkingRecord);

    int insert(ParkingRecord parkingRecord);

    /**
     * 查询某个路段当前在场的停车记录（status='IN'）
     */
    List<ParkingRecord> selectCurrentByParkingIotId(@Param("parkingIotId") Long parkingIotId);

    /**
     * 查询某车牌的停车记录（小程序端：按车牌查历史）
     */
    List<ParkingRecord> selectByPlateNumber(@Param("plateNumber") String plateNumber);

    /**
     * 查询某车牌当前在场的停车记录（status='IN'）
     */
    List<ParkingRecord> selectCurrentByPlateNumber(@Param("plateNumber") String plateNumber);
}
