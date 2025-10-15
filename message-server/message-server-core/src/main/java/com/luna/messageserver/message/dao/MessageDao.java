package com.luna.messageserver.message.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.messageserver.message.domain.entity.Message;
import com.luna.messageserver.message.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {

    /**
     * 根据会话ID获取历史消息（分页）
     */
    public List<Message> getHistoryMessages(Long conversationId, Long cursor, Integer pageSize) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                .eq(Message::getConversationId, conversationId)
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
    public Map<Long, Integer> getMaxSyncIds(List<Long> conversationIds) {
        if (conversationIds == null || conversationIds.isEmpty()) {
            return Map.of();
        }
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("conversation_id", "MAX(sync_id) as sync_id").groupBy("room_id")
                .in("conversation_id", conversationIds);
        return this.list(queryWrapper)
            .stream()
            .collect(Collectors.toMap(Message::getConversationId, Message::getSyncId));
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
    public Long countByConversationId(Long conversationId) {
        return lambdaQuery()
                .eq(Message::getConversationId, conversationId)
                .count();
    }
}
