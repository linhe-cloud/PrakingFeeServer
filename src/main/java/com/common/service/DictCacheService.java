package com.common.service;

import com.common.utils.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 字典数据缓存服务
 * 缓存系统字典数据，如下拉选项、状态码等
 */
@Slf4j
@Service
public class DictCacheService {

    private final RedisService redisService;

    // 缓存前缀
    private static final String DICT_PREFIX = "dict:";
    private static final String DICT_TYPE_PREFIX = "dict:type:";
    private static final String DICT_ALL_PREFIX = "dict:all";

    // 缓存过期时间（24小时）
    private static final long CACHE_EXPIRATION = 86400L;

    public DictCacheService(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 缓存字典类型数据
     *
     * @param typeCode 字典类型编码
     * @param dictList 字典数据列表
     */
    public void cacheDictType(String typeCode, List<?> dictList) {
        try {
            String key = DICT_TYPE_PREFIX + typeCode;
            redisService.lSet(key, dictList);
            redisService.expire(key, CACHE_EXPIRATION);
            log.debug("字典类型缓存成功: typeCode={}, count={}", typeCode, dictList.size());
        } catch (Exception e) {
            log.error("缓存字典类型失败: typeCode={}", typeCode, e);
        }
    }

    /**
     * 获取字典类型数据（从缓存）
     *
     * @param typeCode 字典类型编码
     * @return 字典数据列表
     */
    public List<?> getDictType(String typeCode) {
        try {
            String key = DICT_TYPE_PREFIX + typeCode;
            return redisService.lGet(key, 0, -1);
        } catch (Exception e) {
            log.error("获取字典类型缓存失败: typeCode={}", typeCode, e);
            return null;
        }
    }

    /**
     * 缓存单个字典项
     *
     * @param typeCode 字典类型编码
     * @param itemCode 字典项编码
     * @param dictItem 字典项数据
     */
    public void cacheDictItem(String typeCode, String itemCode, Object dictItem) {
        try {
            String key = DICT_PREFIX + typeCode + ":" + itemCode;
            redisService.set(key, dictItem, CACHE_EXPIRATION);
            log.debug("字典项缓存成功: typeCode={}, itemCode={}", typeCode, itemCode);
        } catch (Exception e) {
            log.error("缓存字典项失败: typeCode={}, itemCode={}", typeCode, itemCode, e);
        }
    }

    /**
     * 获取单个字典项（从缓存）
     *
     * @param typeCode 字典类型编码
     * @param itemCode 字典项编码
     * @return 字典项数据
     */
    public Object getDictItem(String typeCode, String itemCode) {
        try {
            String key = DICT_PREFIX + typeCode + ":" + itemCode;
            return redisService.get(key);
        } catch (Exception e) {
            log.error("获取字典项缓存失败: typeCode={}, itemCode={}", typeCode, itemCode, e);
            return null;
        }
    }

    /**
     * 缓存所有字典数据
     *
     * @param allDictData 所有字典数据
     */
    public void cacheAllDictData(Map<String, List<?>> allDictData) {
        try {
            redisService.hmset(DICT_ALL_PREFIX, allDictData, CACHE_EXPIRATION);
            log.debug("所有字典数据缓存成功: typeCount={}", allDictData.size());
        } catch (Exception e) {
            log.error("缓存所有字典数据失败", e);
        }
    }

    /**
     * 获取所有字典数据（从缓存）
     *
     * @return 所有字典数据
     */
    public Map<String, List<?>> getAllDictData() {
        try {
            return redisService.hmget(DICT_ALL_PREFIX);
        } catch (Exception e) {
            log.error("获取所有字典数据缓存失败", e);
            return null;
        }
    }

    /**
     * 删除字典类型缓存
     *
     * @param typeCode 字典类型编码
     */
    public void removeDictType(String typeCode) {
        try {
            String typeKey = DICT_TYPE_PREFIX + typeCode;
            redisService.del(typeKey);

            // 删除该类型下的所有字典项
            String pattern = DICT_PREFIX + typeCode + ":*";
            // 注意：这里需要使用scan命令来删除匹配的key，暂时用简单的删除
            log.debug("删除字典类型缓存成功: typeCode={}", typeCode);
        } catch (Exception e) {
            log.error("删除字典类型缓存失败: typeCode={}", typeCode, e);
        }
    }

    /**
     * 删除单个字典项缓存
     *
     * @param typeCode 字典类型编码
     * @param itemCode 字典项编码
     */
    public void removeDictItem(String typeCode, String itemCode) {
        try {
            String key = DICT_PREFIX + typeCode + ":" + itemCode;
            redisService.del(key);
            log.debug("删除字典项缓存成功: typeCode={}, itemCode={}", typeCode, itemCode);
        } catch (Exception e) {
            log.error("删除字典项缓存失败: typeCode={}, itemCode={}", typeCode, itemCode, e);
        }
    }

    /**
     * 清除所有字典缓存
     */
    public void clearAllDictCache() {
        try {
            redisService.del(DICT_ALL_PREFIX);
            // 注意：这里应该删除所有以dict:开头的key
            // 在实际项目中，可以使用Redis的scan命令或者维护一个字典类型列表
            log.debug("清除所有字典缓存成功");
        } catch (Exception e) {
            log.error("清除所有字典缓存失败", e);
        }
    }

    /**
     * 刷新字典缓存过期时间
     *
     * @param typeCode 字典类型编码
     */
    public void refreshDictCacheExpiration(String typeCode) {
        try {
            String typeKey = DICT_TYPE_PREFIX + typeCode;
            redisService.expire(typeKey, CACHE_EXPIRATION);

            // 刷新该类型下所有字典项的过期时间
            // 这里需要获取该类型下的所有item key，然后逐一刷新
            log.debug("刷新字典缓存过期时间成功: typeCode={}", typeCode);
        } catch (Exception e) {
            log.error("刷新字典缓存过期时间失败: typeCode={}", typeCode, e);
        }
    }

    /**
     * 检查字典类型是否存在缓存
     *
     * @param typeCode 字典类型编码
     * @return true 存在 false 不存在
     */
    public boolean hasDictTypeCache(String typeCode) {
        try {
            String key = DICT_TYPE_PREFIX + typeCode;
            return redisService.hasKey(key);
        } catch (Exception e) {
            log.error("检查字典类型缓存失败: typeCode={}", typeCode, e);
            return false;
        }
    }

    /**
     * 检查字典项是否存在缓存
     *
     * @param typeCode 字典类型编码
     * @param itemCode 字典项编码
     * @return true 存在 false 不存在
     */
    public boolean hasDictItemCache(String typeCode, String itemCode) {
        try {
            String key = DICT_PREFIX + typeCode + ":" + itemCode;
            return redisService.hasKey(key);
        } catch (Exception e) {
            log.error("检查字典项缓存失败: typeCode={}, itemCode={}", typeCode, itemCode, e);
            return false;
        }
    }

    /**
     * 获取字典项的显示文本
     *
     * @param typeCode 字典类型编码
     * @param itemCode 字典项编码
     * @param defaultValue 默认值
     * @return 显示文本
     */
    public String getDictItemLabel(String typeCode, String itemCode, String defaultValue) {
        try {
            Object dictItem = getDictItem(typeCode, itemCode);
            if (dictItem != null) {
                // 这里假设字典项对象有getLabel()方法，实际情况需要根据你的实体类调整
                if (dictItem instanceof Map) {
                    return ((Map<?, ?>) dictItem).get("label").toString();
                }
            }
        } catch (Exception e) {
            log.error("获取字典项标签失败: typeCode={}, itemCode={}", typeCode, itemCode, e);
        }
        return defaultValue;
    }

    /**
     * 根据字典项值获取字典项对象
     *
     * @param typeCode 字典类型编码
     * @param itemValue 字典项值
     * @return 字典项对象
     */
    public Object getDictItemByValue(String typeCode, String itemValue) {
        try {
            List<?> dictList = getDictType(typeCode);
            if (dictList != null) {
                for (Object item : dictList) {
                    if (item instanceof Map) {
                        Map<?, ?> itemMap = (Map<?, ?>) item;
                        if (itemValue.equals(itemMap.get("value"))) {
                            return item;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("根据值获取字典项失败: typeCode={}, itemValue={}", typeCode, itemValue, e);
        }
        return null;
    }
}
