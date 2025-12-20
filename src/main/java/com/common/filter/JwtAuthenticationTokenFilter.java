package com.common.filter;

import com.common.service.TokenCacheService;
import com.common.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 认证过滤器
 * 集成Redis Token缓存验证
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final TokenCacheService tokenCacheService;

    public JwtAuthenticationTokenFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService,
                                      TokenCacheService tokenCacheService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.tokenCacheService = tokenCacheService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 从 Authorization header 中获取 token
        String authHeader = request.getHeader("Authorization");
        String authToken = null;

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            authToken = authHeader.substring(7);
        }

        if (authToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtUtils.getUsernameFromToken(authToken);

            if (username != null) {
                // 首先验证JWT Token格式和过期时间
                if (jwtUtils.validateToken(authToken, username)) {
                    // 然后通过Redis验证Token是否有效（是否已登录）
                    if (tokenCacheService.isTokenValid(authToken)) {
                        // 从Redis获取用户信息
                        Object userInfo = tokenCacheService.getUserInfoByToken(authToken);
                        if (userInfo != null) {
                            // 如果Redis中有用户信息，直接使用，否则从数据库加载
                            UserDetails userDetails;
                            try {
                                userDetails = userDetailsService.loadUserByUsername(username);
                            } catch (Exception e) {
                                log.warn("从数据库加载用户信息失败: username={}, error={}", username, e.getMessage());
                                filterChain.doFilter(request, response);
                                return;
                            }

                            if (userDetails != null) {
                                UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authentication);

                                // 刷新Token过期时间
                                tokenCacheService.refreshTokenExpiration(authToken);

                                log.debug("Token验证成功，用户认证通过: username={}", username);
                            }
                        } else {
                            log.warn("Redis中未找到Token对应的用户信息: token={}", authToken.substring(0, 20) + "...");
                        }
                    } else {
                        log.debug("Token在Redis中无效或已过期: token={}", authToken.substring(0, 20) + "...");
                    }
                } else {
                    log.debug("JWT Token格式验证失败或已过期: token={}", authToken.substring(0, 20) + "...");
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
