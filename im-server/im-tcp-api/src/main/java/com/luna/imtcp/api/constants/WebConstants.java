package com.luna.imtcp.api.constants;

public class WebConstants {
    /**
     * UserSign，格式：appId:UserSign:
     */
    public static final String UserSign = ":userSign:";
    /**
     * 用户登录端消息通道信息
     */
    public static final String UserLoginChannel = "signal/channel/LOGIN_USER_INNER_QUEUE";
    /**
     * 用户session：格式为 appId + userSessionConstants + 用户 ID
     * 例如：10001:userSessionConstants:userId
     */
    public static final String UserSessionConstants = ":userSession:";
    /**
     * 缓存客户端消息防重，格式： appId + :cacheMessage: + messageId
     */
    public static final String CacheMessage = ":cacheMessage:";
    /**
     * 缓存群组成员列表
     */
    public static final String GroupMembers = ":groupMembers:";
}
