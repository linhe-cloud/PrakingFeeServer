package com.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 服务工具类
 */
@Slf4j
@Component
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // =============================common============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return 布尔值
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0 && key != null) {
                // 使用局部变量确保类型安全
                String nonNullKey = key;
                redisTemplate.expire(nonNullKey, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("设置缓存过期时间失败: key={}, time={}", key, time, e);
            return false;
        }
    }

    /**
     * 根据 key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) -1代表永久有效，-2代表key不存在
     */
    public long getExpire(String key) {
        if (key == null) {
            return -2;
        }
        // 使用局部变量确保类型安全
        String nonNullKey = key;
        Long expire = redisTemplate.getExpire(nonNullKey, TimeUnit.SECONDS);
        return expire != null ? expire : -2;
    }

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            if (key == null) {
                return false;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            return Boolean.TRUE.equals(redisTemplate.hasKey(nonNullKey));
        } catch (Exception e) {
            log.error("判断key是否存在失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     * @return 删除的key数量
     */
    public long del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                // 使用中间变量避免 @NonNull 类型安全警告
                String singleKey = key[0];
                if (singleKey != null) {
                    Boolean result = redisTemplate.delete(singleKey);
                    return Boolean.TRUE.equals(result) ? 1L : 0L;
                }
                return 0L;
            } else {
                // 过滤null元素并转换为List，避免 @NonNull 类型安全警告
                List<String> nonNullKeys = Arrays.stream(key)
                        .filter(Objects::nonNull)
                        .toList();
                if (!nonNullKeys.isEmpty()) {
                    Long count = redisTemplate.delete(nonNullKeys);
                    return count != null ? count : 0L;
                }
                return 0L;
            }
        }
        return 0L;
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        if (key == null) {
            return null;
        }
        // 使用局部变量确保类型安全
        String nonNullKey = key;
        return redisTemplate.opsForValue().get(nonNullKey);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            if (key == null) {
                log.warn("尝试设置缓存但key为null");
                return false;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            // value 可以为 null（Redis 支持存储 null 值），但需要确保类型安全
            Object nonNullValue = value != null ? value : "";
            redisTemplate.opsForValue().set(nonNullKey, nonNullValue);
            return true;
        } catch (Exception e) {
            log.error("设置缓存失败: key={}, value={}", key, value, e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (key == null) {
                log.warn("尝试设置缓存但key为null");
                return false;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            // value 可以为 null（Redis 支持存储 null 值），但需要确保类型安全
            Object nonNullValue = value != null ? value : "";
            if (time > 0) {
                redisTemplate.opsForValue().set(nonNullKey, nonNullValue, time, TimeUnit.SECONDS);
            } else {
                set(nonNullKey, nonNullValue);
            }
            return true;
        } catch (Exception e) {
            log.error("设置缓存失败: key={}, value={}, time={}", key, value, time, e);
            return false;
        }
    }

    /**
     * 设置分布式锁（SET NX EX 原子操作）
     * 仅当 key 不存在时才设置，并同时设置过期时间
     *
     * @param key        键
     * @param value      值
     * @param expireTime 过期时间(秒)
     * @return true 设置成功（获取锁成功） false 设置失败（key已存在，获取锁失败）
     */
    public boolean setIfAbsent(String key, Object value, long expireTime) {
        try {
            if (key == null) {
                log.warn("尝试设置分布式锁但key为null");
                return false;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            Object nonNullValue = value != null ? value : "";
            
            // 使用 setIfAbsent 实现 SET NX EX 原子操作
            Boolean result = redisTemplate.opsForValue().setIfAbsent(
                    nonNullKey, 
                    nonNullValue, 
                    expireTime, 
                    TimeUnit.SECONDS
            );
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("设置分布式锁失败: key={}, value={}, expireTime={}", key, value, expireTime, e);
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return 递增后的值
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        if (key == null) {
            throw new IllegalArgumentException("key不能为null");
        }
        // 使用局部变量确保类型安全
        String nonNullKey = key;
        Long result = redisTemplate.opsForValue().increment(nonNullKey, delta);
        return result != null ? result : 0L;
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return 递减后的值
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        if (key == null) {
            throw new IllegalArgumentException("key不能为null");
        }
        // 使用局部变量确保类型安全
        String nonNullKey = key;
        Long result = redisTemplate.opsForValue().increment(nonNullKey, -delta);
        return result != null ? result : 0L;
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        if (key == null || item == null) {
            return null;
        }
        // 使用局部变量确保类型安全
        String nonNullKey = key;
        String nonNullItem = item;
        return redisTemplate.opsForHash().get(nonNullKey, nonNullItem);
    }

    /**
     * 获取 hashKey 对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> hmget(String key) {
        if (key == null) {
            return Collections.emptyMap();
        }
        // 使用局部变量确保类型安全
        String nonNullKey = key;
        return (Map<K, V>) redisTemplate.opsForHash().entries(nonNullKey);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, ?> map) {
        try {
            if (key == null || map == null) {
                log.warn("尝试设置Hash缓存但key或map为null");
                return false;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            // 使用中间变量确保 Map 类型安全,避免 @NonNull 警告
            Map<? extends Object, ? extends Object> nonNullMap = map;
            redisTemplate.opsForHash().putAll(nonNullKey, nonNullMap);
            return true;
        } catch (Exception e) {
            log.error("设置Hash缓存失败: key={}, map={}", key, map, e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, ?> map, long time) {
        try {
            if (key == null || map == null) {
                log.warn("尝试设置Hash缓存但key或map为null");
                return false;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            // 使用中间变量确保 Map 类型安全,避免 @NonNull 警告
            Map<? extends Object, ? extends Object> nonNullMap = map;
            redisTemplate.opsForHash().putAll(nonNullKey, nonNullMap);
            if (time > 0) {
                expire(nonNullKey, time);
            }
            return true;
        } catch (Exception e) {
            log.error("设置Hash缓存失败: key={}, map={}, time={}", key, map, time, e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            if (key == null || item == null) {
                log.warn("尝试设置Hash项缓存但key或item为null");
                return false;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            String nonNullItem = item;
            // value 可以为 null，但需要确保类型安全
            Object nonNullValue = value != null ? value : "";
            redisTemplate.opsForHash().put(nonNullKey, nonNullItem, nonNullValue);
            return true;
        } catch (Exception e) {
            log.error("设置Hash项缓存失败: key={}, item={}, value={}", key, item, value, e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如枟不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            if (key == null || item == null) {
                log.warn("尝试设置Hash项缓存但key或item为null");
                return false;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            String nonNullItem = item;
            // value 可以为 null，但需要确保类型安全
            Object nonNullValue = value != null ? value : "";
            redisTemplate.opsForHash().put(nonNullKey, nonNullItem, nonNullValue);
            if (time > 0) {
                expire(nonNullKey, time);
            }
            return true;
        } catch (Exception e) {
            log.error("设置Hash项缓存失败: key={}, item={}, value={}, time={}", key, item, value, time, e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        if (key == null || item == null || item.length == 0) {
            return;
        }
        // 使用局部变量确保类型安全
        String nonNullKey = key;
        // 过滤null元素并转换为数组，避免 @NonNull 类型安全警告
        Object[] nonNullItems = Arrays.stream(item)
                .filter(Objects::nonNull)
                .toArray();
        if (nonNullItems.length > 0) {
            redisTemplate.opsForHash().delete(nonNullKey, nonNullItems);
        }
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        if (key == null || item == null) {
            return false;
        }
        // 使用局部变量确保类型安全
        String nonNullKey = key;
        String nonNullItem = item;
        return redisTemplate.opsForHash().hasKey(nonNullKey, nonNullItem);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return 递增后的值
     */
    public double hincr(String key, String item, double by) {
        if (key == null || item == null) {
            throw new IllegalArgumentException("key和item不能为null");
        }
        // 使用局部变量确保类型安全
        String nonNullKey = key;
        String nonNullItem = item;
        Double result = redisTemplate.opsForHash().increment(nonNullKey, nonNullItem, by);
        return result != null ? result : 0.0;
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少几(大于0)
     * @return 递减后的值
     */
    public double hdecr(String key, String item, double by) {
        if (key == null || item == null) {
            throw new IllegalArgumentException("key和item不能为null");
        }
        // 使用局部变量确保类型安全
        String nonNullKey = key;
        String nonNullItem = item;
        Double result = redisTemplate.opsForHash().increment(nonNullKey, nonNullItem, -by);
        return result != null ? result : 0.0;
    }

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return Set集合
     */
    public Set<Object> sGet(String key) {
        try {
            if (key == null) {
                return null;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            return redisTemplate.opsForSet().members(nonNullKey);
        } catch (Exception e) {
            log.error("获取Set缓存失败: key={}", key, e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            if (key == null) {
                return false;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            Object nonNullValue = value != null ? value : "";
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(nonNullKey, nonNullValue));
        } catch (Exception e) {
            log.error("查询Set成员失败: key={}, value={}", key, value, e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            if (key == null || values == null || values.length == 0) {
                return 0;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            // 过滤null元素并转换为数组,避免 @NonNull 类型安全警告
            Object[] nonNullValues = Arrays.stream(values)
                    .filter(Objects::nonNull)
                    .toArray();
            if (nonNullValues.length == 0) {
                return 0;
            }
            Long count = redisTemplate.opsForSet().add(nonNullKey, nonNullValues);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("设置Set缓存失败: key={}, values={}", key, Arrays.toString(values), e);
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            if (key == null || values == null || values.length == 0) {
                return 0;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            // 过滤null元素并转换为数组,避免 @NonNull 类型安全警告
            Object[] nonNullValues = Arrays.stream(values)
                    .filter(Objects::nonNull)
                    .toArray();
            if (nonNullValues.length == 0) {
                return 0;
            }
            Long count = redisTemplate.opsForSet().add(nonNullKey, nonNullValues);
            if (time > 0) {
                expire(nonNullKey, time);
            }
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("设置Set缓存失败: key={}, time={}, values={}", key, time, Arrays.toString(values), e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return 长度
     */
    public long sGetSetSize(String key) {
        try {
            if (key == null) {
                return 0;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            Long size = redisTemplate.opsForSet().size(nonNullKey);
            return size != null ? size : 0L;
        } catch (Exception e) {
            log.error("获取Set大小失败: key={}", key, e);
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            if (key == null || values == null || values.length == 0) {
                return 0;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            // 过滤null元素并转换为数组,避免 @NonNull 类型安全警告
            Object[] nonNullValues = Arrays.stream(values)
                    .filter(Objects::nonNull)
                    .toArray();
            if (nonNullValues.length == 0) {
                return 0;
            }
            Long count = redisTemplate.opsForSet().remove(nonNullKey, nonNullValues);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("移除Set成员失败: key={}, values={}", key, Arrays.toString(values), e);
            return 0;
        }
    }

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return List集合
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            if (key == null) {
                return null;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            return redisTemplate.opsForList().range(nonNullKey, start, end);
        } catch (Exception e) {
            log.error("获取List缓存失败: key={}, start={}, end={}", key, start, end, e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return 长度
     */
    public long lGetListSize(String key) {
        try {
            if (key == null) {
                return 0;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            Long size = redisTemplate.opsForList().size(nonNullKey);
            return size != null ? size : 0L;
        } catch (Exception e) {
            log.error("获取List大小失败: key={}", key, e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return 值
     */
    public Object lGetIndex(String key, long index) {
        try {
            if (key == null) {
                return null;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            return redisTemplate.opsForList().index(nonNullKey, index);
        } catch (Exception e) {
            log.error("获取List索引值失败: key={}, index={}", key, index, e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return 布尔值
     */
    public boolean lSet(String key, Object value) {
        try {
            if (key == null) {
                return false;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            // value 可以为 null，但需要确保类型安全
            Object nonNullValue = value != null ? value : "";
            redisTemplate.opsForList().rightPush(nonNullKey, nonNullValue);
            return true;
        } catch (Exception e) {
            log.error("设置List缓存失败: key={}, value={}", key, value, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return 布尔值
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            if (key == null) {
                return false;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            // value 可以为 null，但需要确保类型安全
            Object nonNullValue = value != null ? value : "";
            redisTemplate.opsForList().rightPush(nonNullKey, nonNullValue);
            if (time > 0) {
                expire(nonNullKey, time);
            }
            return true;
        } catch (Exception e) {
            log.error("设置List缓存失败: key={}, value={}, time={}", key, value, time, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return 布尔值
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            if (key == null || value == null) {
                log.warn("尝试设置List缓存但key或value为null");
                return false;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            // 使用展开运算符确保类型安全
            redisTemplate.opsForList().rightPushAll(nonNullKey, value.toArray());
            return true;
        } catch (Exception e) {
            log.error("设置List缓存失败: key={}, value={}", key, value, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return 布尔值
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            if (key == null || value == null) {
                log.warn("尝试设置List缓存但key或value为null");
                return false;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            // 使用展开运算符确保类型安全
            redisTemplate.opsForList().rightPushAll(nonNullKey, value.toArray());
            if (time > 0) {
                expire(nonNullKey, time);
            }
            return true;
        } catch (Exception e) {
            log.error("设置List缓存失败: key={}, value={}, time={}", key, value, time, e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return 布尔值
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            if (key == null) {
                return false;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;
            // value 可以为 null，但需要确保类型安全
            Object nonNullValue = value != null ? value : "";
            redisTemplate.opsForList().set(nonNullKey, index, nonNullValue);
            return true;
        } catch (Exception e) {
            log.error("更新List索引值失败: key={}, index={}, value={}", key, index, value, e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            if (key == null) {
                return 0;
            }
            // 使用局部变量确保类型安全
            String nonNullKey = key;

            Object nonNullValue = value != null ? value : "";
            Long removedCount = redisTemplate.opsForList().remove(nonNullKey, count, nonNullValue);
            return removedCount != null ? removedCount : 0L;
        } catch (Exception e) {
            log.error("移除List值失败: key={}, count={}, value={}", key, count, value, e);
            return 0;
        }
    }

    // ============================Lua脚本执行=============================

    /**
     * 执行Lua脚本
     *
     * @param script Lua脚本内容
     * @param keys   Redis键列表
     * @param args   参数列表
     * @return 脚本执行结果
     */
    public <T> T executeLuaScript(String script, List<String> keys, Object... args) {
        try {
            DefaultRedisScript<T> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType((Class<T>) Object.class);
            return redisTemplate.execute(redisScript, keys, args);
        } catch (Exception e) {
            log.error("执行Lua脚本失败: script={}, keys={}, args={}", script, keys, Arrays.toString(args), e);
            return null;
        }
    }

    /**
     * 执行Lua脚本（指定返回类型）
     *
     * @param script     Lua脚本内容
     * @param resultType 返回类型
     * @param keys       Redis键列表
     * @param args       参数列表
     * @return 脚本执行结果
     */
    public <T> T executeLuaScript(String script, Class<T> resultType, List<String> keys, Object... args) {
        try {
            DefaultRedisScript<T> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(resultType);
            return redisTemplate.execute(redisScript, keys, args);
        } catch (Exception e) {
            log.error("执行Lua脚本失败: script={}, keys={}, args={}", script, keys, Arrays.toString(args), e);
            return null;
        }
    }
}
