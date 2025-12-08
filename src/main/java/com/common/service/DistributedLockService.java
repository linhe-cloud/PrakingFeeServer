package com.common.service;

import com.common.utils.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

/**
 * 分布式锁服务
 * 基于 Redis 实现的分布式锁
 */
@Slf4j
@Service
public class DistributedLockService {

    private final RedisService redisService;

    // 锁前缀
    private static final String LOCK_PREFIX = "lock:";

    // 默认锁过期时间（30秒）
    private static final long DEFAULT_LOCK_TIMEOUT = 30L;

    // 获取锁的最大等待时间（10秒）
    private static final long DEFAULT_WAIT_TIME = 10L;

    // Lua脚本：释放锁（原子操作）
    // 只有当锁的值与传入的值匹配时才删除锁
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";

    // Lua脚本：扩展锁过期时间（原子操作）
    // 只有当锁的值与传入的值匹配时才设置过期时间
    private static final String EXTEND_LOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('expire', KEYS[1], ARGV[2]) " +
            "else " +
            "    return 0 " +
            "end";

    public DistributedLockService(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 尝试获取锁（立即返回）
     *
     * @param lockKey 锁的key
     * @return 锁标识，如果获取失败返回null
     */
    public String tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_LOCK_TIMEOUT);
    }

    /**
     * 尝试获取锁（立即返回）
     *
     * @param lockKey    锁的key
     * @param expireTime 锁过期时间（秒）
     * @return 锁标识，如果获取失败返回null
     */
    public String tryLock(String lockKey, long expireTime) {
        String lockValue = UUID.randomUUID().toString();
        String key = LOCK_PREFIX + lockKey;

        try {
            // 使用 setIfAbsent 实现原子性的 SET NX EX 操作
            boolean success = redisService.setIfAbsent(key, lockValue, expireTime);
            if (success) {
                log.debug("获取分布式锁成功: lockKey={}, lockValue={}", lockKey, lockValue);
                return lockValue;
            } else {
                log.debug("获取分布式锁失败: lockKey={}", lockKey);
                return null;
            }
        } catch (Exception e) {
            log.error("获取分布式锁异常: lockKey={}", lockKey, e);
            return null;
        }
    }

    /**
     * 尝试获取锁（带等待时间）
     *
     * @param lockKey    锁的key
     * @param expireTime 锁过期时间（秒）
     * @param waitTime   等待时间（秒）
     * @return 锁标识，如果获取失败返回null
     */
    public String tryLock(String lockKey, long expireTime, long waitTime) {
        String lockValue = UUID.randomUUID().toString();
        String key = LOCK_PREFIX + lockKey;

        long startTime = System.currentTimeMillis();
        long endTime = startTime + waitTime * 1000;

        try {
            while (System.currentTimeMillis() < endTime) {
                // 使用 setIfAbsent 实现原子性的 SET NX EX 操作
                boolean success = redisService.setIfAbsent(key, lockValue, expireTime);
                if (success) {
                    log.debug("获取分布式锁成功: lockKey={}, lockValue={}, waitTime={}ms",
                            lockKey, lockValue, System.currentTimeMillis() - startTime);
                    return lockValue;
                }

                // 等待一段时间后重试（避免频繁请求Redis）
                Thread.sleep(100);
            }

            log.debug("获取分布式锁超时: lockKey={}, waitTime={}ms", lockKey, waitTime * 1000);
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁被中断: lockKey={}", lockKey, e);
            return null;
        } catch (Exception e) {
            log.error("获取分布式锁异常: lockKey={}", lockKey, e);
            return null;
        }
    }

    /**
     * 释放锁（使用Lua脚本保证原子性）
     *
     * @param lockKey   锁的key
     * @param lockValue 锁的标识（用于验证锁的持有者）
     * @return true 释放成功 false 释放失败
     */
    public boolean unlock(String lockKey, String lockValue) {
        String key = LOCK_PREFIX + lockKey;

        try {
            // 使用Lua脚本保证原子性，避免get和del之间的竞态条件
            Long result = redisService.executeLuaScript(
                    UNLOCK_SCRIPT,
                    Long.class,
                    Collections.singletonList(key),
                    lockValue
            );

            if (result != null && result == 1) {
                log.debug("释放分布式锁成功: lockKey={}, lockValue={}", lockKey, lockValue);
                return true;
            } else {
                log.warn("释放分布式锁失败，锁不存在或不属于当前线程: lockKey={}, lockValue={}",
                        lockKey, lockValue);
                return false;
            }
        } catch (Exception e) {
            log.error("释放分布式锁异常: lockKey={}, lockValue={}", lockKey, lockValue, e);
            return false;
        }
    }

    /**
     * 扩展锁的过期时间（使用Lua脚本保证原子性）
     *
     * @param lockKey    锁的key
     * @param lockValue  锁的标识
     * @param expireTime 新的过期时间（秒）
     * @return true 扩展成功 false 扩展失败
     */
    public boolean extendLock(String lockKey, String lockValue, long expireTime) {
        String key = LOCK_PREFIX + lockKey;

        try {
            // 使用Lua脚本保证原子性，避免get和expire之间的竞态条件
            Long result = redisService.executeLuaScript(
                    EXTEND_LOCK_SCRIPT,
                    Long.class,
                    Collections.singletonList(key),
                    lockValue,
                    String.valueOf(expireTime)
            );

            if (result != null && result == 1) {
                log.debug("扩展分布式锁过期时间成功: lockKey={}, lockValue={}, expireTime={}",
                        lockKey, lockValue, expireTime);
                return true;
            } else {
                log.warn("扩展分布式锁过期时间失败，锁不存在或不属于当前线程: lockKey={}, lockValue={}",
                        lockKey, lockValue);
                return false;
            }
        } catch (Exception e) {
            log.error("扩展分布式锁过期时间异常: lockKey={}, lockValue={}", lockKey, lockValue, e);
            return false;
        }
    }

    /**
     * 检查锁是否存在
     *
     * @param lockKey 锁的key
     * @return true 存在 false 不存在
     */
    public boolean isLocked(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        try {
            return redisService.hasKey(key);
        } catch (Exception e) {
            log.error("检查锁状态异常: lockKey={}", lockKey, e);
            return false;
        }
    }

    /**
     * 获取锁的剩余过期时间
     *
     * @param lockKey 锁的key
     * @return 剩余时间（秒），-2表示锁不存在，-1表示永不过期
     */
    public long getLockExpireTime(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        try {
            return redisService.getExpire(key);
        } catch (Exception e) {
            log.error("获取锁过期时间异常: lockKey={}", lockKey, e);
            return -2;
        }
    }

    /**
     * 在锁保护下执行任务
     *
     * @param lockKey    锁的key
     * @param expireTime 锁过期时间（秒）
     * @param waitTime   等待时间（秒）
     * @param task       要执行的任务
     * @return 任务执行结果
     */
    public <T> T executeWithLock(String lockKey, long expireTime, long waitTime, LockTask<T> task) {
        String lockValue = tryLock(lockKey, expireTime, waitTime);
        if (lockValue == null) {
            throw new RuntimeException("获取分布式锁失败: " + lockKey);
        }

        try {
            return task.execute();
        } finally {
            unlock(lockKey, lockValue);
        }
    }

    /**
     * 在锁保护下执行任务（使用默认时间）
     *
     * @param lockKey 锁的key
     * @param task    要执行的任务
     * @return 任务执行结果
     */
    public <T> T executeWithLock(String lockKey, LockTask<T> task) {
        return executeWithLock(lockKey, DEFAULT_LOCK_TIMEOUT, DEFAULT_WAIT_TIME, task);
    }

    /**
     * 锁任务接口
     */
    @FunctionalInterface
    public interface LockTask<T> {
        T execute();
    }

    /**
     * 强制释放锁（仅在确认锁持有者已失效时使用）
     *
     * @param lockKey 锁的key
     * @return true 释放成功 false 释放失败
     */
    public boolean forceUnlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        try {
            redisService.del(key);
            log.warn("强制释放分布式锁: lockKey={}", lockKey);
            return true;
        } catch (Exception e) {
            log.error("强制释放分布式锁异常: lockKey={}", lockKey, e);
            return false;
        }
    }

    /**
     * 获取锁的持有者标识
     *
     * @param lockKey 锁的key
     * @return 锁持有者标识，如果锁不存在返回null
     */
    public String getLockHolder(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        try {
            Object value = redisService.get(key);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.error("获取锁持有者异常: lockKey={}", lockKey, e);
            return null;
        }
    }
}
