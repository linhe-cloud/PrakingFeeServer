package com.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.common.result.Result;
import com.common.service.TokenCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登出成功处理器
 * 集成Token缓存清除功能
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    private final TokenCacheService tokenCacheService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                               Authentication authentication) throws IOException {
        String username = authentication != null ? authentication.getName() : null;

        // 尝试从请求头获取Token并清除
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (username != null) {
                // 清除用户的Token缓存
                tokenCacheService.removeAllUserTokens(username);
                log.info("用户Token缓存已清除: {}", username);
            } else {
                // 如果无法获取用户名，直接清除Token
                tokenCacheService.removeToken(token);
                log.info("Token缓存已清除");
            }
        } else if (username != null) {
            // 如果没有Token头，但有用户名，清除用户的所有Token
            tokenCacheService.removeAllUserTokens(username);
            log.info("用户所有Token缓存已清除: {}", username);
        }

        log.info("用户登出成功: {}", username != null ? username : "未知用户");

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        Result<Void> result = Result.success("登出成功");
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
