package com.system.controller;
        
import com.common.result.Result;
import com.common.service.TokenCacheService;
import com.common.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 集成Redis Token缓存功能
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final TokenCacheService tokenCacheService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        // 设置认证信息
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成 JWT Token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails.getUsername());

        // 将Token和用户信息存储到Redis中
        tokenCacheService.storeToken(userDetails.getUsername(), token, userDetails);

        // 返回登录结果
        Map<String, Object> data = new HashMap<>();
        data.put("token", jwtUtils.getTokenHead() + " " + token);
        data.put("user", userDetails);

        log.info("用户登录成功，已缓存Token: {}", userDetails.getUsername());
        return Result.success("登录成功", data);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserDetails> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return Result.success(userDetails);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // 从Redis中清除用户的所有Token
            tokenCacheService.removeAllUserTokens(username);

            // 清除SecurityContext
            SecurityContextHolder.clearContext();

            log.info("用户登出成功，已清除Token缓存: {}", username);
            return Result.success("登出成功");
        }

        return Result.success("登出成功");
    }

    /**
     * 登录请求体
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
