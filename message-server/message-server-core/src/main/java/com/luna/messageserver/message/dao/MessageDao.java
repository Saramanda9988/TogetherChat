package com.luna.messageserver.message.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.messageserver.message.domain.entity.Message;
import com.luna.messageserver.message.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {

    /**
     * 根据会话ID获取历史消息（分页）
     */
    public List<Message> getHistoryMessages(Long groupId, Long cursor, Integer pageSize) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                .eq(Message::getGroupId, groupId)
                .orderByDesc(Message::getMessageId)
                .last("LIMIT " + pageSize);
        
        if (cursor != null) {
            wrapper.lt(Message::getMessageId, cursor);
        }
        
        return list(wrapper);
    }

    /**
     * 根据消息ID获取单条消息
     */
    public Message getByMessageId(Long messageId) {
        return lambdaQuery()
                .eq(Message::getMessageId, messageId)
                .one();
    }

    /**
     * 获取最大的syncId
     */
    public Integer getMaxSyncId() {
        Message maxSyncMessage = lambdaQuery()
                .orderByDesc(Message::getSyncId)
                .last("LIMIT 1")
                .one();
        return maxSyncMessage != null ? maxSyncMessage.getSyncId() : 0;
    }

    /**
     * 获取最大的messageId
     */
    public Long getMaxMessageId() {
        Message maxMessage = lambdaQuery()
                .orderByDesc(Message::getMessageId)
                .last("LIMIT 1")
                .one();
        return maxMessage != null ? maxMessage.getMessageId() : 0L;
    }

    /**
     * 保存消息
     */
    public boolean saveMessage(Message message) {
        return save(message);
    }

    /**
     * 根据会话ID统计消息数量
     */
    public Long countByGroupId(Long groupId) {
        return lambdaQuery()
                .eq(Message::getGroupId, groupId)
                .count();
    }
}
