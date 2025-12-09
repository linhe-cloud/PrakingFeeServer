package com.common.aspect;

import com.common.annotation.Cacheable;
import com.common.annotation.CacheEvict;
import com.common.utils.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 缓存切面
 * 处理缓存注解的AOP逻辑
 */
@Slf4j
@Aspect
@Component
public class CacheAspect {

    private final RedisService redisService;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    // 缓存key前缀
    private static final String CACHE_PREFIX = "cache:";

    public CacheAspect(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 定义切点：所有使用了缓存注解的方法
     */
    @Pointcut("@annotation(com.parking.manager.common.annotation.Cacheable) || " +
              "@annotation(com.parking.manager.common.annotation.CacheEvict)")
    public void cachePointcut() {}

    /**
     * 环绕通知
     */
    @Around("cachePointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 处理缓存清除注解
        CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
        if (cacheEvict != null) {
            handleCacheEvict(joinPoint, cacheEvict);
            return joinPoint.proceed();
        }

        // 处理缓存注解
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        if (cacheable != null) {
            return handleCacheable(joinPoint, cacheable);
        }

        return joinPoint.proceed();
    }

    /**
     * 处理缓存注解
     */
    private Object handleCacheable(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
        // 检查缓存条件
        if (StringUtils.hasText(cacheable.condition())) {
            if (!evaluateCondition(cacheable.condition(), joinPoint)) {
                log.debug("缓存条件不满足，跳过缓存: {}", cacheable.condition());
                return joinPoint.proceed();
            }
        }

        // 生成缓存key
        String cacheKey = generateCacheKey(joinPoint, cacheable);
        if (cacheKey == null || cacheKey.isEmpty()) {
            log.warn("缓存key为空，跳过缓存");
            return joinPoint.proceed();
        }

        // 尝试从缓存获取数据（确保key不为null）
        Object cachedResult = redisService.get(cacheKey);
        if (cachedResult != null) {
            log.debug("缓存命中: key={}", cacheKey);
            return cachedResult;
        }

        // 缓存未命中，执行方法
        log.debug("缓存未命中，执行方法: key={}", cacheKey);
        Object result = joinPoint.proceed();

        // 将结果存入缓存
        if (result != null) {
            redisService.set(cacheKey, result, cacheable.expire());
            log.debug("结果已缓存: key={}, expire={}s", cacheKey, cacheable.expire());
        }

        return result;
    }

    /**
     * 处理缓存清除注解
     */
    private void handleCacheEvict(ProceedingJoinPoint joinPoint, CacheEvict cacheEvict) {
        // 检查清除条件
        if (StringUtils.hasText(cacheEvict.condition())) {
            if (!evaluateCondition(cacheEvict.condition(), joinPoint)) {
                log.debug("清除条件不满足，跳过清除: {}", cacheEvict.condition());
                return;
            }
        }

        if (cacheEvict.allEntries()) {
            // 清除所有相关缓存
            clearAllRelatedCache(joinPoint, cacheEvict);
        } else {
            // 清除指定缓存
            String cacheKey = generateCacheKey(joinPoint, cacheEvict);
            if (cacheKey != null && !cacheKey.isEmpty()) {
                redisService.del(cacheKey);
                log.debug("缓存已清除: key={}", cacheKey);
            } else {
                log.warn("缓存key为空，跳过清除");
            }
        }
    }

    /**
     * 生成缓存key
     */
    private String generateCacheKey(ProceedingJoinPoint joinPoint, Cacheable cacheable) {
        StringBuilder keyBuilder = new StringBuilder(CACHE_PREFIX);

        // 添加缓存名称
        if (StringUtils.hasText(cacheable.cacheName())) {
            keyBuilder.append(cacheable.cacheName()).append(":");
        }

        // 添加key前缀
        if (StringUtils.hasText(cacheable.key())) {
            keyBuilder.append(cacheable.key());
        } else {
            // 使用类名和方法名作为默认key
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            // 确保className和methodName不为null或空
            String safeClassName = (className != null && !className.isEmpty()) ? className : "Unknown";
            String safeMethodName = (methodName != null && !methodName.isEmpty()) ? methodName : "unknown";
            keyBuilder.append(safeClassName).append(":").append(safeMethodName);
        }

        // 添加方法参数
        if (cacheable.useArgs()) {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                keyBuilder.append(":");
                for (int i = 0; i < args.length; i++) {
                    if (i > 0) {
                        keyBuilder.append(",");
                    }
                    keyBuilder.append(args[i] != null ? args[i].toString() : "null");
                }
            }
        }

        return keyBuilder.toString();
    }

    /**
     * 生成缓存key（用于CacheEvict）
     */
    private String generateCacheKey(ProceedingJoinPoint joinPoint, CacheEvict cacheEvict) {
        StringBuilder keyBuilder = new StringBuilder(CACHE_PREFIX);

        // 添加缓存名称
        if (StringUtils.hasText(cacheEvict.cacheName())) {
            keyBuilder.append(cacheEvict.cacheName()).append(":");
        }

        // 添加key前缀
        if (StringUtils.hasText(cacheEvict.key())) {
            keyBuilder.append(cacheEvict.key());
        } else {
            // 使用类名和方法名作为默认key
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            // 确保className和methodName不为null或空
            String safeClassName = (className != null && !className.isEmpty()) ? className : "Unknown";
            String safeMethodName = (methodName != null && !methodName.isEmpty()) ? methodName : "unknown";
            keyBuilder.append(safeClassName).append(":").append(safeMethodName);
        }

        return keyBuilder.toString();
    }

    /**
     * 清除所有相关缓存
     */
    private void clearAllRelatedCache(ProceedingJoinPoint joinPoint, CacheEvict cacheEvict) {
        // 这里简化实现，实际项目中可能需要根据缓存名称或key模式来清除
        String cacheName = cacheEvict.cacheName();
        String safeCacheName = (cacheName != null && !cacheName.isEmpty()) ? cacheName : "*";
        String pattern = CACHE_PREFIX + safeCacheName + ":*";
        // 注意：Redis原生不支持通配符删除，这里需要使用scan命令
        // 暂时记录日志，实际实现需要更复杂的逻辑
        log.debug("清除所有相关缓存: pattern={}", pattern);
    }

    /**
     * 评估SpEL条件表达式
     */
    private boolean evaluateCondition(String condition, ProceedingJoinPoint joinPoint) {
        // 首先检查条件表达式本身是否为null或空
        if (condition == null || condition.trim().isEmpty()) {
            log.warn("条件表达式为空,默认返回false");
            return false;
        }
        
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            if (method != null) {
                // 获取参数名（可能为null）
                String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
                Object[] args = joinPoint.getArgs();
               
                // 创建评估上下文
                EvaluationContext context = new StandardEvaluationContext();
                // 确保parameterNames和args都不为null后再处理
                if (parameterNames != null && args != null && parameterNames.length > 0) {
                    for (int i = 0; i < parameterNames.length && i < args.length; i++) {
                        // 确保参数名不为null且不为空字符串
                        String paramName = parameterNames[i];
                        if (paramName != null && !paramName.isEmpty() && args[i] != null) {
                            // 此时paramName已确保不为null,可以安全传递给setVariable
                            context.setVariable(paramName, args[i]);
                        }
                    }
                }

                // 解析和评估表达式（condition已在方法开始处检查不为null）
                Expression expression = expressionParser.parseExpression(condition);
                Object result = expression.getValue(context);
                log.debug("条件表达式评估结果: condition={}, result={}", condition, result);
                return Boolean.TRUE.equals(result);
            }
            return false;
        } catch (Exception e) {
            log.error("评估条件表达式失败: condition={}, error={}", condition, e.getMessage());
            return false;
        }
    }
}
