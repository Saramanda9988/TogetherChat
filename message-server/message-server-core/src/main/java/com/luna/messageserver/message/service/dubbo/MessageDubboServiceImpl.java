package com.luna.messageserver.message.service.dubbo;

import com.luna.messageserver.api.dto.MessageDTO;
import com.luna.messageserver.api.service.MessageDubboService;
import com.luna.messageserver.message.dao.MessageDao;
import com.luna.messageserver.message.domain.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 消息服务Dubbo实现类
 * 提供消息相关的远程调用服务实现
 *
 * @author LunaRain_079
 * @since 2025-10-15
 */
@Slf4j
@Component
@RequiredArgsConstructor
@DubboService(group = "message-service", interfaceClass = MessageDubboService.class)
@Service
public class MessageDubboServiceImpl implements MessageDubboService {

    private final MessageDao messageDao;

    /**
     * 获取message表中最大的syncId
     */
    @Override
    public Integer getMaxSyncId() {
        Integer maxSyncId = messageDao.getMaxSyncId();
        log.debug("获取最大syncId: {}", maxSyncId);
        return maxSyncId;
    }

    /**
     * 获取message表中最大的messageId
     */
    @Override
    public Long getMaxMessageId() {
        Long maxMessageId = messageDao.getMaxMessageId();
        log.debug("获取最大messageId: {}", maxMessageId);
        return maxMessageId;
    }

    /**
     * 保存消息到message表中
     */
    @Override
    public boolean saveMessage(MessageDTO messageDTO) {
        if (messageDTO == null) {
            log.error("保存消息失败: messageDTO为空");
            return false;
        }

        Message message = convertToEntity(messageDTO);
        boolean result = messageDao.saveMessage(message);
        
        if (result) {
            log.info("Dubbo保存消息成功: messageId={}, groupId={}, userId={}", 
                    message.getMessageId(), message.getGroupId(), message.getUserId());
        } else {
            log.error("Dubbo保存消息失败: messageId={}, groupId={}, userId={}", 
                    message.getMessageId(), message.getGroupId(), message.getUserId());
        }
        
        return result;
    }

    /**
     * 根据消息ID获取消息
     */
    @Override
    public MessageDTO getMessageById(Long messageId) {
        if (messageId == null) {
            log.error("获取消息失败: messageId为空");
            return null;
        }

        Message message = messageDao.getByMessageId(messageId);
        if (message == null) {
            log.warn("消息不存在: messageId={}", messageId);
            return null;
        }

        return convertToDTO(message);
    }

    /**
     * 根据群组ID统计消息数量
     */
    @Override
    public Long countMessagesByGroupId(Long groupId) {
        if (groupId == null) {
            log.error("统计消息数量失败: groupId为空");
            return 0L;
        }

        Long count = messageDao.countByGroupId(groupId);
        log.debug("群组 {} 的消息数量: {}", groupId, count);
        return count;
    }

    /**
     * 转换为实体对象
     */
    private static Message convertToEntity(MessageDTO dto) {
        return Message.builder()
                .messageId(dto.getMessageId())
                .syncId(dto.getSyncId())
                .groupId(dto.getGroupId())
                .userId(dto.getUserId())
                .status(dto.getStatus())
                .messageType(dto.getMessageType())
                .replyMessage(dto.getReplyMessage())
                .content(dto.getContent())
                .createTime(dto.getCreateTime())
                .updateTime(dto.getUpdateTime())
                .build();
    }

    /**
     * 转换为DTO对象
     */
    private static MessageDTO convertToDTO(Message entity) {
        return MessageDTO.builder()
                .messageId(entity.getMessageId())
                .syncId(entity.getSyncId())
                .groupId(entity.getGroupId())
                .userId(entity.getUserId())
                .status(entity.getStatus())
                .messageType(entity.getMessageType())
                .replyMessage(entity.getReplyMessage())
                .content(entity.getContent())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}