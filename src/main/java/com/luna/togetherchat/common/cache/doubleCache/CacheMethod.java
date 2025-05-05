package com.luna.togetherchat.common.cache.doubleCache;

public enum CacheMethod {
    CACHEABLE,   //存取
    PUT,         //只存
    EVICT,       //删除
    UPDATE_IF_PRESENT, // 存在则更新
}