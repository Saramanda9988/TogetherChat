package com.luna.togetherchat.common.cache.doubleCache;

public enum CachePattern {
    BYPASS,         // 旁路【高一致性场景】
    WRITE_THROUGH,  // 穿透【冷热分区】：
    ASYNC,          //异步【高频写】
    FALLBACK,       // 兜底【高可用】
    READ_ONLY,      //只读【100%命中，最终一致性】

}
