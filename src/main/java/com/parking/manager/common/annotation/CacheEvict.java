package com.parking.manager.common.annotation;

import java.lang.annotation.*;

/**
 * 缓存清除注解
 * 用于标注需要清除缓存的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvict {

    /**
     * 缓存key前缀
     */
    String key() default "";

    /**
     * 是否清除所有匹配的缓存
     * 默认false，只清除指定的key
     */
    boolean allEntries() default false;

    /**
     * 清除条件，支持SpEL表达式
     * 只有当条件为true时才进行清除
     */
    String condition() default "";

    /**
     * 缓存名称，用于区分不同类型的缓存
     */
    String cacheName() default "default";
}
