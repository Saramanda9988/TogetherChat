package com.luna.togetherchat.common.cache.doubleCache;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DoubleCache {
    String cacheNames();
    String key();
    long l2TimeOut() default 300;
    CacheMethod type();
}