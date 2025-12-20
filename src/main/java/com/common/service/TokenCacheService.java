package com.common.service;

import com.common.utils.JwtUtils;
import com.common.utils.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Token 缓存服务
 * 使用 Redis 存储和管理 JWT Token
 */
@Slf4j
@Service
public class TokenCacheService {

    private final RedisService redisService;
    private final JwtUtils jwtUtils;

    // Token 缓存前缀
    private static final String TOKEN_PREFIX = "auth:token:";

    // 用户 Token 列表前缀
    private static final String USER_TOKENS_PREFIX = "auth:user_tokens:";

    // Token 过期时间（秒）
    @Value("${jwt.expiration:7200}")
    private Long tokenExpiration;

    public TokenCacheService(RedisService redisService, JwtUtils jwtUtils) {
        this.redisService = redisService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 存储 Token 信息
     *
     * @param username 用户名
     * @param token    JWT Token
     * @param userInfo 用户信息对象（注意：这里只存储用户名，不存储整个UserDetails对象）
     */
    public void storeToken(String username, String token, Object userInfo) {
        try {
            String tokenKey = TOKEN_PREFIX + token;
            String userTokensKey = USER_TOKENS_PREFIX + username;

            // 只存储用户名，避免UserDetails序列化问题
            redisService.set(tokenKey, username, tokenExpiration);

            // 将 Token 添加到用户的 Token 列表中
            redisService.sSet(userTokensKey, token);
            redisService.expire(userTokensKey, tokenExpiration);

            log.debug("Token 存储成功: username={}, token={}", username, token);
        } catch (Exception e) {
            log.error("存储Token失败: username={}, token={}", username, token, e);
        }
    }

    /**
     * 获取 Token 对应的用户信息
     *
     * @param token JWT Token
     * @return 用户信息对象
     */
    public Object getUserInfoByToken(String token) {
        try {
            String tokenKey = TOKEN_PREFIX + token;
            return redisService.get(tokenKey);
        } catch (Exception e) {
            log.error("获取Token用户信息失败: token={}", token, e);
            return null;
        }
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token
     * @return true 有效 false 无效
     */
    public boolean isTokenValid(String token) {
        try {
            // 首先验证 JWT Token 的格式和过期时间
            if (!jwtUtils.validateToken(token, jwtUtils.getUsernameFromToken(token))) {
                return false;
            }

            // 然后检查 Redis 中是否存在该 Token
            String tokenKey = TOKEN_PREFIX + token;
            return redisService.hasKey(tokenKey);
        } catch (Exception e) {
            log.error("验证Token失败: token={}", token, e);
            return false;
        }
    }

    /**
     * 移除单个 Token
     *
     * @param token JWT Token
     */
    public void removeToken(String token) {
        try {
            String tokenKey = TOKEN_PREFIX + token;
            Object userInfo = redisService.get(tokenKey);

            if (userInfo != null) {
                // 从 Redis 中删除 Token
                redisService.del(tokenKey);

                // 从用户的 Token 列表中移除
                String username = jwtUtils.getUsernameFromToken(token);
                String userTokensKey = USER_TOKENS_PREFIX + username;
                redisService.setRemove(userTokensKey, token);

                log.debug("Token 移除成功: token={}", token);
            }
        } catch (Exception e) {
            log.error("移除Token失败: token={}", token, e);
        }
    }

    /**
     * 移除用户的所有 Token（用于登出所有设备）
     *
     * @param username 用户名
     */
    public void removeAllUserTokens(String username) {
        try {
            String userTokensKey = USER_TOKENS_PREFIX + username;

            // 获取用户的所有 Token
            java.util.Set<Object> tokens = redisService.sGet(userTokensKey);
            if (tokens != null && !tokens.isEmpty()) {
                // 删除所有 Token
                for (Object token : tokens) {
                    String tokenKey = TOKEN_PREFIX + token.toString();
                    redisService.del(tokenKey);
                }

                // 删除用户的 Token 列表
                redisService.del(userTokensKey);

                log.debug("用户所有Token移除成功: username={}, tokenCount={}", username, tokens.size());
            }
        } catch (Exception e) {
            log.error("移除用户所有Token失败: username={}", username, e);
        }
    }

    /**
     * 刷新 Token 过期时间
     *
     * @param token JWT Token
     */
    public void refreshTokenExpiration(String token) {
        try {
            String tokenKey = TOKEN_PREFIX + token;
            if (redisService.hasKey(tokenKey)) {
                redisService.expire(tokenKey, tokenExpiration);

                // 刷新用户 Token 列表的过期时间
                String username = jwtUtils.getUsernameFromToken(token);
                String userTokensKey = USER_TOKENS_PREFIX + username;
                redisService.expire(userTokensKey, tokenExpiration);

                log.debug("Token过期时间刷新成功: token={}", token);
            }
        } catch (Exception e) {
            log.error("刷新Token过期时间失败: token={}", token, e);
        }
    }

    /**
     * 获取用户的在线 Token 数量
     *
     * @param username 用户名
     * @return Token 数量
     */
    public long getUserOnlineTokenCount(String username) {
        try {
            String userTokensKey = USER_TOKENS_PREFIX + username;
            return redisService.sGetSetSize(userTokensKey);
        } catch (Exception e) {
            log.error("获取用户在线Token数量失败: username={}", username, e);
            return 0;
        }
    }

    /**
     * 清理过期 Token
     * 这个方法可以定期调用来清理过期的 Token 数据
     */
    public void cleanExpiredTokens() {
        try {
            // 这里主要依赖 Redis 的自动过期机制
            // 如果需要主动清理，可以遍历所有 key，但这比较耗性能
            // 在生产环境中，可以考虑使用 Redis 的过期事件通知机制
            log.debug("清理过期Token任务执行完成");
        } catch (Exception e) {
            log.error("清理过期Token失败", e);
        }
    }

    /**
     * 检查用户是否已经登录（是否有有效的 Token）
     *
     * @param username 用户名
     * @return true 已登录 false 未登录
     */
    public boolean isUserLoggedIn(String username) {
        try {
            String userTokensKey = USER_TOKENS_PREFIX + username;
            return redisService.sGetSetSize(userTokensKey) > 0;
        } catch (Exception e) {
            log.error("检查用户登录状态失败: username={}", username, e);
            return false;
        }
    }
}
