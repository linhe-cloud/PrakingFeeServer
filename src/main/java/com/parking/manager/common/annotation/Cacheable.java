package com.parking.manager.common.annotation;

import java.lang.annotation.*;

/**
 * 缓存注解
 * 用于标注需要缓存的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cacheable {

    /**
     * 缓存key前缀
     */
    String key() default "";

    /**
     * 缓存过期时间（秒）
     * 默认5分钟
     */
    long expire() default 300;

    /**
     * 是否使用参数作为缓存key的一部分
     * 默认true
     */
    boolean useArgs() default true;

    /**
     * 缓存条件，支持SpEL表达式
     * 只有当条件为true时才进行缓存
     */
    String condition() default "";

    /**
     * 缓存名称，用于区分不同类型的缓存
     */
    String cacheName() default "default";
}
