package com.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 用户详情服务实现（从数据库加载用户）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // 从数据库查询用户
            String sql = "SELECT username, password, status FROM sys_user WHERE username = ? AND is_deleted = 0";
            
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                String dbUsername = rs.getString("username");
                String dbPassword = rs.getString("password");
                int status = rs.getInt("status");
                
                // 构建 UserDetails
                return User.builder()
                        .username(dbUsername)
                        .password(dbPassword)
                        .disabled(status == 0) // status=0 表示禁用
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                        .build();
            }, username);
            
        } catch (Exception e) {
            log.error("查询用户失败: {}", username, e);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
    }
}
