package com.luna.togetherchat.common.constant;

/**
 * Description: 计数器类型常量，对应 Redis Key
 * Date: 2023-07-15
 */
public class CounterType {
    
    /**
     * 点赞计数的 Redis Key
     */
    public static final String LIKE = "like";
    
    /**
     * 评论计数的 Redis Key
     */
    public static final String COMMENT = "comment";
    
    /**
     * 收藏计数的 Redis Key
     */
    public static final String COLLECT = "collect";
}
