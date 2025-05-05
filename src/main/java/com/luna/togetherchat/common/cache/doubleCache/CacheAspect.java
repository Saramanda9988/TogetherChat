package com.luna.togetherchat.common.cache.doubleCache;

import com.github.benmanes.caffeine.cache.Cache;
import com.luna.togetherchat.common.cache.ELParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class CacheAspect {

    @Autowired
    private Cache<String, Object> cache;

    private final RedisTemplate<String, Object> redisTemplate;

    @Pointcut("@annotation(com.luna.togetherchat.common.cache.doubleCache.DoubleCache)")
    public void cacheAspect() {
    }

    @Around("cacheAspect()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        DoubleCache annotation = method.getAnnotation(DoubleCache.class);

        String realKey = getString(point, signature, annotation);

        switch (annotation.type()) {
            case CACHEABLE -> {
                // 查询Caffeine
                Object caffeineCache = cache.getIfPresent(realKey);
                if (Objects.nonNull(caffeineCache)) {
                    return caffeineCache;
                }
                // 查询Redis
                Object redisCache = redisTemplate.opsForValue().get(realKey);
                if (Objects.nonNull(redisCache)) {
                    cache.put(realKey, redisCache);
                    return redisCache;
                }
                // 查询数据库
                Object object = point.proceed();
                if (Objects.nonNull(object)) {
                    redisTemplate.opsForValue().set(realKey, object, annotation.l2TimeOut(), TimeUnit.SECONDS);
                    cache.put(realKey, object);
                }
                return object;
            }
            case UPDATE_IF_PRESENT -> {
                Object object = point.proceed();

                Object caffeineCache = cache.getIfPresent(realKey);
                if (Objects.nonNull(caffeineCache)) {
                    cache.put(realKey, object);
                }

                Object redisCache = redisTemplate.opsForValue().get(realKey);

                if (Objects.nonNull(redisCache)) {
                    redisTemplate.opsForValue().set(realKey, object, annotation.l2TimeOut(), TimeUnit.SECONDS);
                }

                return object;
            }
            case PUT -> {
                Object object = point.proceed();
                redisTemplate.opsForValue().set(realKey, object, annotation.l2TimeOut(), TimeUnit.SECONDS);
                cache.put(realKey, object);
                return object;
            }
            case EVICT -> {
                redisTemplate.delete(realKey);
                cache.invalidate(realKey);
                return point.proceed();
            }
            default -> throw new IllegalStateException("Unexpected value: " + annotation.type());
        }
    }

    private static @NotNull String getString(ProceedingJoinPoint point, MethodSignature signature, DoubleCache annotation) {
        //拼接解析springEl表达式的map
        String[] paramNames = signature.getParameterNames();
        Object[] args = point.getArgs();
        TreeMap<String, Object> treeMap = new TreeMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            treeMap.put(paramNames[i], args[i]);
        }
        String elResult = ELParser.parse(annotation.key(), treeMap);
        return annotation.cacheNames() + CacheConstant.COLON + elResult;
    }
}