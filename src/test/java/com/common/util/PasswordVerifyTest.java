package com.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码验证测试
 */
public class PasswordVerifyTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 数据库中的密码
        String dbPassword = "$2a$10$dXJ3SW6G7P50lGLkhl8lqeAtHz68lp4ZX.Lb2MlLXLEhPHm7YOmvi";
        
        // 测试不同的明文密码
        String[] testPasswords = {"admin123", "admin", "123456", "password"};
        
        System.out.println("========================================");
        System.out.println("数据库密码: " + dbPassword);
        System.out.println("========================================");
        
        for (String password : testPasswords) {
            boolean matches = encoder.matches(password, dbPassword);
            System.out.println("明文: " + password + " -> " + (matches ? "✓ 匹配" : "✗ 不匹配"));
        }
        
        System.out.println("========================================");
        System.out.println("生成新的 admin123 密码：");
        String newPassword = encoder.encode("admin123");
        System.out.println(newPassword);
        System.out.println("========================================");
        System.out.println("\nSQL更新语句：");
        System.out.println("UPDATE sys_user SET password = '" + newPassword + "' WHERE username = 'admin';");
    }
}
