package com.luna.messageserver.message.service;

import com.luna.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.common.exception.BusinessException;
import com.luna.messageserver.message.dao.MessageDao;
import com.luna.messageserver.message.domain.entity.Message;
import com.luna.messageserver.message.domain.request.MessageHistoryRequest;
import com.luna.messageserver.message.domain.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  消息服务类
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-10-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageDao messageDao;

    /**
     * 获取会话历史消息
     */
    public CursorPageBaseResponse<MessageResponse> getHistoryMessages(Long conversationId, MessageHistoryRequest request) {
        if (conversationId == null) {
            throw new BusinessException("会话ID不能为空");
        }

        Integer pageSize = request.getPageSize() != null ? request.getPageSize() : 20;
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 20; // 默认20条，最大100条
        }

        // 查询消息列表
        List<Message> messages = messageDao.getHistoryMessages(conversationId, request.getCursor(), pageSize + 1);
        
        // 判断是否还有更多数据
        boolean hasMore = messages.size() > pageSize;
        if (hasMore) {
            messages = messages.subList(0, pageSize);
        }

        // 反转消息顺序，让最新的消息在前面
        Collections.reverse(messages);

        // 转换为响应对象
        List<MessageResponse> responses = messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        // 构建游标分页响应
        CursorPageBaseResponse<MessageResponse> result = new CursorPageBaseResponse<>();
        result.setList(responses);
        result.setIsLast(!hasMore);
        
        if (!messages.isEmpty()) {
            // 设置下一页的游标为最后一条消息的ID
            result.setCursor(messages.get(messages.size() - 1).getMessageId().doubleValue());
        }

        log.info("获取历史消息成功, conversationId: {}, cursor: {}, pageSize: {}, resultSize: {}", 
                conversationId, request.getCursor(), pageSize, responses.size());

        return result;
    }

    /**
     * 根据消息ID获取单条消息
     */
    public MessageResponse getMessageById(Long messageId) {
        if (messageId == null) {
            throw new BusinessException("消息ID不能为空");
        }

        Message message = messageDao.getByMessageId(messageId);
        if (message == null) {
            throw new BusinessException("消息不存在");
        }

        return convertToResponse(message);
    }

    /**
     * 获取最大的messageId
     */
    public Long getMaxMessageId() {
        return messageDao.getMaxMessageId();
    }

    /**
     * 保存消息
     */
    public boolean saveMessage(Message message) {
        if (message == null) {
            throw new BusinessException("消息不能为空");
        }
        
        boolean result = messageDao.saveMessage(message);
        if (result) {
            log.info("保存消息成功, messageId: {}, conversationId: {}, userId: {}",
                    message.getMessageId(), message.getConversationId(), message.getUserId());
        } else {
            log.error("保存消息失败, messageId: {}, conversationId: {}, userId: {}",
                    message.getMessageId(), message.getConversationId(), message.getUserId());
        }
        
        return result;
    }

    /**
     * 转换为响应对象
     */
    private MessageResponse convertToResponse(Message message) {
        return MessageResponse.builder()
                .messageId(message.getMessageId())
                .syncId(message.getSyncId())
                .conversationId(message.getConversationId())
                .userId(message.getUserId())
                .status(message.getStatus())
                .messageType(message.getMessageType())
                .replyMessage(message.getReplyMessage())
                .content(message.getContent())
                .createTime(message.getCreateTime())
                .updateTime(message.getUpdateTime())
                .build();
    }
}
