package com.charge.service;

import com.charge.entity.DTO.CalculateFeeRequest;
import com.charge.entity.VO.CalculateFeeResponse;

/**
 * 计费服务接口
 * 定义与“收费/计费”相关的业务能力
 */
public interface ChargeService {
    /**
     * 根据入场记录ID计算停车费用
     * @param inRecordId 入场记录ID
     * @return 停车费用
     */
    CalculateFeeResponse calculateParkingFee(CalculateFeeRequest request);
    
}
