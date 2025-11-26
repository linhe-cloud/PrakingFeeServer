package com.parking.manager.system.controller;

import com.parking.manager.common.result.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/hello")
    public Result<Void> hello() {
        return Result.success("Hello, Parking Manager!");
    }
}
