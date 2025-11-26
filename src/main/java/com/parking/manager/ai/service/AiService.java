package com.parking.manager.ai.service;

import org.springframework.stereotype.Service;

/**
 * AI服务类
 */
@Service
public class AiService {

    /**
     * 车牌识别方法
     * @param imagePath 图片路径
     * @return 识别结果
     */
    public String recognizeLicensePlate(String imagePath) {
        // TODO: 实现车牌识别逻辑
        return "识别结果示例";
    }
}
