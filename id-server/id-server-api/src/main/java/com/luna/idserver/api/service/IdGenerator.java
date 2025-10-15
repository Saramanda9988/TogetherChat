package com.luna.idserver.api.service;

/**
 * 用户服务Dubbo接口定义
 * 提供用户相关的远程调用服务
 */
public interface IdGenerator {
    /**
     * 生成群聊消息ID
     */
    Long generateMessageId() throws RuntimeException;

    /**
     * 生成对话同步ID
     */
    Long generateConversationSyncId(Long conversationId) throws RuntimeException;

    /**
     * 生成雪花id
     */
    String generateSnowflakeId();
}