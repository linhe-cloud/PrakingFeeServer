package com.parking.manager.common.service;

import com.parking.manager.common.utils.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 用户权限缓存服务
 * 缓存用户的角色、权限信息，避免频繁查询数据库
 */
@Slf4j
@Service
public class UserPermissionCacheService {

    private final RedisService redisService;

    // 缓存前缀
    private static final String USER_ROLES_PREFIX = "auth:user_roles:";
    private static final String USER_PERMISSIONS_PREFIX = "auth:user_permissions:";
    private static final String USER_MENU_PREFIX = "auth:user_menu:";

    // 缓存过期时间（1小时）
    private static final long CACHE_EXPIRATION = 3600L;

    public UserPermissionCacheService(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 缓存用户角色信息
     *
     * @param username 用户名
     * @param roles    角色列表
     */
    public void cacheUserRoles(String username, List<String> roles) {
        try {
            String key = USER_ROLES_PREFIX + username;
            redisService.sSet(key, roles.toArray());
            redisService.expire(key, CACHE_EXPIRATION);
            log.debug("用户角色缓存成功: username={}, roles={}", username, roles);
        } catch (Exception e) {
            log.error("缓存用户角色失败: username={}, roles={}", username, roles, e);
        }
    }

    /**
     * 获取用户角色信息（从缓存）
     *
     * @param username 用户名
     * @return 角色集合
     */
    @SuppressWarnings("unchecked")
    public Set<String> getUserRoles(String username) {
        try {
            String key = USER_ROLES_PREFIX + username;
            Set<Object> roles = redisService.sGet(key);
            if (roles != null && !roles.isEmpty()) {
                return (Set<String>) (Set<?>) roles;
            }
        } catch (Exception e) {
            log.error("获取用户角色缓存失败: username={}", username, e);
        }
        return null;
    }

    /**
     * 缓存用户权限信息
     *
     * @param username    用户名
     * @param permissions 权限列表
     */
    public void cacheUserPermissions(String username, List<String> permissions) {
        try {
            String key = USER_PERMISSIONS_PREFIX + username;
            redisService.sSet(key, permissions.toArray());
            redisService.expire(key, CACHE_EXPIRATION);
            log.debug("用户权限缓存成功: username={}, permissions={}", username, permissions);
        } catch (Exception e) {
            log.error("缓存用户权限失败: username={}, permissions={}", username, permissions, e);
        }
    }

    /**
     * 获取用户权限信息（从缓存）
     *
     * @param username 用户名
     * @return 权限集合
     */
    @SuppressWarnings("unchecked")
    public Set<String> getUserPermissions(String username) {
        try {
            String key = USER_PERMISSIONS_PREFIX + username;
            Set<Object> permissions = redisService.sGet(key);
            if (permissions != null && !permissions.isEmpty()) {
                return (Set<String>) (Set<?>) permissions;
            }
        } catch (Exception e) {
            log.error("获取用户权限缓存失败: username={}", username, e);
        }
        return null;
    }

    /**
     * 缓存用户菜单信息
     *
     * @param username 用户名
     * @param menus    菜单列表
     */
    public void cacheUserMenus(String username, List<?> menus) {
        try {
            String key = USER_MENU_PREFIX + username;
            redisService.lSet(key, menus);
            redisService.expire(key, CACHE_EXPIRATION);
            log.debug("用户菜单缓存成功: username={}, menuCount={}", username, menus.size());
        } catch (Exception e) {
            log.error("缓存用户菜单失败: username={}", username, e);
        }
    }

    /**
     * 获取用户菜单信息（从缓存）
     *
     * @param username 用户名
     * @return 菜单列表
     */
    @SuppressWarnings("unchecked")
    public List<?> getUserMenus(String username) {
        try {
            String key = USER_MENU_PREFIX + username;
            List<Object> menus = redisService.lGet(key, 0, -1);
            if (menus != null && !menus.isEmpty()) {
                return menus;
            }
        } catch (Exception e) {
            log.error("获取用户菜单缓存失败: username={}", username, e);
        }
        return null;
    }

    /**
     * 清除用户的所有缓存信息
     *
     * @param username 用户名
     */
    public void clearUserCache(String username) {
        try {
            String rolesKey = USER_ROLES_PREFIX + username;
            String permissionsKey = USER_PERMISSIONS_PREFIX + username;
            String menuKey = USER_MENU_PREFIX + username;

            redisService.del(rolesKey, permissionsKey, menuKey);
            log.debug("清除用户缓存成功: username={}", username);
        } catch (Exception e) {
            log.error("清除用户缓存失败: username={}", username, e);
        }
    }

    /**
     * 检查用户是否有指定权限
     *
     * @param username   用户名
     * @param permission 权限字符串
     * @return true 有权限 false 无权限
     */
    public boolean hasPermission(String username, String permission) {
        try {
            Set<String> permissions = getUserPermissions(username);
            if (permissions != null) {
                return permissions.contains(permission);
            }
        } catch (Exception e) {
            log.error("检查用户权限失败: username={}, permission={}", username, permission, e);
        }
        return false;
    }

    /**
     * 检查用户是否有指定角色
     *
     * @param username 用户名
     * @param role     角色字符串
     * @return true 有角色 false 无角色
     */
    public boolean hasRole(String username, String role) {
        try {
            Set<String> roles = getUserRoles(username);
            if (roles != null) {
                return roles.contains(role);
            }
        } catch (Exception e) {
            log.error("检查用户角色失败: username={}, role={}", username, role, e);
        }
        return false;
    }

    /**
     * 批量清除多个用户的缓存
     *
     * @param usernames 用户名列表
     */
    public void clearUsersCache(List<String> usernames) {
        try {
            for (String username : usernames) {
                clearUserCache(username);
            }
            log.debug("批量清除用户缓存成功: userCount={}", usernames.size());
        } catch (Exception e) {
            log.error("批量清除用户缓存失败: usernames={}", usernames, e);
        }
    }

    /**
     * 刷新用户缓存过期时间
     *
     * @param username 用户名
     */
    public void refreshUserCacheExpiration(String username) {
        try {
            String rolesKey = USER_ROLES_PREFIX + username;
            String permissionsKey = USER_PERMISSIONS_PREFIX + username;
            String menuKey = USER_MENU_PREFIX + username;

            redisService.expire(rolesKey, CACHE_EXPIRATION);
            redisService.expire(permissionsKey, CACHE_EXPIRATION);
            redisService.expire(menuKey, CACHE_EXPIRATION);

            log.debug("刷新用户缓存过期时间成功: username={}", username);
        } catch (Exception e) {
            log.error("刷新用户缓存过期时间失败: username={}", username, e);
        }
    }

    /**
     * 获取缓存统计信息
     *
     * @return 缓存统计信息
     */
    public CacheStats getCacheStats() {
        try {
            CacheStats stats = new CacheStats();
            // 这里可以添加更详细的统计信息
            // 比如缓存命中率等，需要额外的计数器
            return stats;
        } catch (Exception e) {
            log.error("获取缓存统计信息失败", e);
            return new CacheStats();
        }
    }

    /**
     * 缓存统计信息类
     */
    public static class CacheStats {
        // 可以添加各种统计字段
        // 如命中次数、未命中次数等
    }
}
