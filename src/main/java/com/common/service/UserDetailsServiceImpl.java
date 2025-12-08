package com.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * 用户详情服务实现（暂时使用内存用户，后续对接数据库）
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 暂时使用硬编码用户，后续从数据库查询
        if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    .password("$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2") // 密码：admin123
                    .roles("ADMIN")
                    .build();
        } else if ("user".equals(username)) {
            return User.builder()
                    .username("user")
                    .password("$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2") // 密码：admin123
                    .roles("USER")
                    .build();
        }

        throw new UsernameNotFoundException("用户不存在: " + username);
    }
}
