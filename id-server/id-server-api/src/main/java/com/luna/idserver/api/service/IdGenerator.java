package com.luna.idserver.api.service;

/**
 * 用户服务Dubbo接口定义
 * 提供用户相关的远程调用服务
 */
public interface IdGenerator {
    /**
     * 生成群聊消息ID
     */
    Long generateGroupMessageId();

    /**
     * 生成私聊消息ID
     */
    Long generateP2PMessageId();

    /**
     * 生成对话同步ID
     */
    Long generateRoomSyncId(Long groupId);

    /**
     * 生成雪花id
     */
    String generateSnowflakeId();
}