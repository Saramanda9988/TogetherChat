package com.luna.togetherchat.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description: 内容类型枚举
 * Date: 2023-07-15
 */
@AllArgsConstructor
@Getter
public enum ContentEnums {
    
    FEED(1, "短聊天"),
    COMMENT(2, "评论"),
    COLLECTION_LIST(3, "收藏列表"),
    MESSAGE(4, "消息"),
    COMMUNITY_POST(5, "社区帖子");
    
    private final Integer code;
    private final String desc;
    
    /**
     * 通过code获取枚举
     */
    public static ContentEnums getByCode(Integer code) {
        for (ContentEnums typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }
}
