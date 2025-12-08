package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动程序
 *
 * @author parking
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan("com.parking.manager.**.mapper")
@ComponentScan(basePackages = "com.parking.manager")
public class ParkingManagerApplication {
    public static void main(String[] args) {
        // 设置系统属性
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(ParkingManagerApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  停车收费管理系统启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
