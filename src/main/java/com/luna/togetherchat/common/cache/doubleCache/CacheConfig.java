package com.luna.togetherchat.common.cache.doubleCache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    @Primary()
    @Bean("caffeineCacheManager")
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // 性能优化相关，这个东西可以自己调整
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .expireAfterWrite(60, TimeUnit.SECONDS)//过期时间
                .maximumSize(10_000));
        return cacheManager;
    }

    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                .initialCapacity(1_000)//初始大小
                .maximumSize(100_000)//最大数量
                .expireAfterWrite(60, TimeUnit.SECONDS)//过期时间
                .build();
    }
}
