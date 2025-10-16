package com.luna.messageserver.message.event.consumer;

import com.luna.common.constant.MQConstant;
import com.luna.messageserver.api.dto.MessageDTO;
import com.luna.messageserver.message.domain.entity.Message;
import com.luna.messageserver.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消息保存消费者
 * 处理从MQ接收到的消息保存请求，进行异步落库
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageSaveConsumer {

    private final MessageService messageService;

    /**
     * 消费消息保存事件，进行异步落库
     * 
     * @param messageDTO 消息DTO
     */
    @RabbitListener(queuesToDeclare = @Queue(
            value = MQConstant.MESSAGE_SAVE_QUEUE,
            durable = "true"
    ))
    public void handleMessageSave(MessageDTO messageDTO) {
        log.info("接收到消息保存事件: messageId={}, conversationId={}, userId={}", 
                messageDTO.getMessageId(), messageDTO.getConversationId(), messageDTO.getUserId());
        
        try {
            // 转换为实体对象
            Message message = convertToEntity(messageDTO);
            
            // 调用服务层保存消息
            boolean success = messageService.saveMessage(message);
            
            if (success) {
                log.info("异步落库成功: messageId={}, conversationId={}, userId={}", 
                        message.getMessageId(), message.getConversationId(), message.getUserId());
            } else {
                log.error("异步落库失败: messageId={}, conversationId={}, userId={}", 
                        message.getMessageId(), message.getConversationId(), message.getUserId());
                throw new RuntimeException("消息保存失败");
            }
            
        } catch (Exception e) {
            log.error("处理消息保存事件失败: messageId={}, error={}", messageDTO.getMessageId(), e.getMessage(), e);
            // 让消息进入死信队列或重试
            throw e;
        }
    }

    /**
     * 转换DTO为实体对象
     */
    private Message convertToEntity(MessageDTO dto) {
        return Message.builder()
                .messageId(dto.getMessageId())
                .syncId(dto.getSyncId())
                .conversationId(dto.getConversationId())
                .userId(dto.getUserId())
                .status(dto.getStatus())
                .messageType(dto.getMessageType())
                .replyMessage(dto.getReplyMessage())
                .content(dto.getContent())
                .createTime(dto.getCreateTime())
                .updateTime(dto.getUpdateTime())
                .build();
    }
}
