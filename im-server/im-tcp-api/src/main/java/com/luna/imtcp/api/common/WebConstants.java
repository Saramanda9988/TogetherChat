package com.luna.imtcp.api.common;

public class WebConstants {

    public static class ChannelConstants {

        /**
         * channel 绑定的 userId Key
         */
        public static final String UserId = "userId";

        /**
         * channel 绑定的 appId Key
         */
        public static final String AppId = "appId";

        /**
         * channel 绑定的端类型
         */
        public static final String ClientType = "clientType";

        /**
         * channel 绑定的读写时间
         */
        public static final String ReadTime = "readTime";

        /**
         * channel 绑定的 imei 号，标识用户登录设备号
         */
        public static final String imei = "imei";

        /**
         * channel 绑定的 clientType 和 imei Key
         */
        public static final String ClientImei = "clientImei";
    }

    public static class RedisConstants {
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
         * 缓存离线消息 获取用户消息队列 格式：appId + :offlineMessage: + fromId / toId
         */
        public static final String OfflineMessage = ":offlineMessage:";
        /**
         * 缓存群组成员列表
         */
        public static final String GroupMembers = ":groupMembers:";
        /**
         * 用户所有模块的偏序前缀
         */
        public static final String SeqPrefix = ":seq:";
    }
}
