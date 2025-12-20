package com.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码编码工具类
 * 用于生成 BCrypt 密码
 */
public class PasswordEncoderTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成 admin123 的 BCrypt 密码
        String rawPassword = "admin123";
        String encoded = encoder.encode(rawPassword);
        
        System.out.println("========================================");
        System.out.println("明文密码: " + rawPassword);
        System.out.println("BCrypt 密文: " + encoded);
        System.out.println("========================================");
        System.out.println("\nSQL 更新语句：");
        System.out.println("UPDATE sys_user SET password = '" + encoded + "' WHERE username = 'admin';");
        System.out.println("========================================");
    }
}
