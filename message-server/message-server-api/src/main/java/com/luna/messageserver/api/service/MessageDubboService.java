package com.luna.messageserver.api.service;

import com.luna.messageserver.api.dto.MessageDTO;

import java.util.Map;

/**
 * 消息服务Dubbo接口定义
 * 提供消息相关的远程调用服务
 */
public interface MessageDubboService {

    /**
     * 获取message表中最大的messageId
     * @return 最大的messageId
     */
    Long getMaxMessageId();

    /**
     * 获取每个会话的最大syncId
     * @return conversationId -> syncId的映射
     */
    Map<Long, Integer> getMaxSyncIds();

    /**
     * 保存消息到message表中
     * @param messageDTO 消息数据
     * @return 是否保存成功
     */
    boolean saveMessage(MessageDTO messageDTO);

    /**
     * 根据消息ID获取消息
     * @param messageId 消息ID
     * @return 消息信息
     */
    MessageDTO getMessageById(Long messageId);

    /**
     * 根据群组ID统计消息数量
     * @param groupId 群组ID
     * @return 消息数量
     */
    Long countMessagesByGroupId(Long groupId);

    /**
     * 更新消息状态
     * @param messageId 消息ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateMessageStatus(Long messageId, Integer status);
}