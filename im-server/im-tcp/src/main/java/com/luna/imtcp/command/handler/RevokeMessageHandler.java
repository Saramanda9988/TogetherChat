package com.luna.imtcp.command.handler;

import com.luna.common.constant.MQConstant;
import com.luna.common.utils.AssertUtil;
import com.luna.common.utils.JsonUtils;
import com.luna.common.utils.MQProducer;
import com.luna.conversationserver.api.service.ConversationDubboService;
import com.luna.imtcp.api.enums.CommandType;
import com.luna.imtcp.api.vo.WebMessage;
import com.luna.imtcp.api.vo.msgBody.RevokeMessageBody;
import com.luna.imtcp.command.CommandHandler;
import com.luna.imtcp.service.PushService;
import com.luna.messageserver.api.dto.MessageDTO;
import com.luna.messageserver.api.enums.MessageStatusEnum;
import com.luna.messageserver.api.service.MessageDubboService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RevokeMessageHandler implements CommandHandler {
    
    @DubboReference(group = "message-service")
    private MessageDubboService messageDubboService;
    
    @DubboReference(group = "conversation-service")
    private ConversationDubboService conversationDubboService;
    
    private final PushService pushService;
    private final MQProducer mqProducer;

    @Override
    public void handle(WebMessage message) {
        log.info("RevokeMessageHandler收到撤回消息请求: {}", message);
        
        try {
            // 1. 解析消息体
            RevokeMessageBody revokeMsg = JsonUtils.toObj(message.getMessagePack(), RevokeMessageBody.class);
            if (revokeMsg == null) {
                log.error("撤回消息体解析失败: {}", message.getMessagePack());
                return;
            }

            // 2. 验证必要参数
            AssertUtil.isNotNull(revokeMsg.getMessageId(), "消息ID不能为空");
            AssertUtil.isNotNull(revokeMsg.getOperatingUserId(), "操作用户ID不能为空");
            AssertUtil.isNotNull(revokeMsg.getConversationId(), "会话ID不能为空");

            // 3. 获取原消息信息
            MessageDTO originalMessage = messageDubboService.getMessageById(revokeMsg.getMessageId());
            if (originalMessage == null) {
                log.warn("要撤回的消息不存在: messageId={}", revokeMsg.getMessageId());
                return;
            }

            // 4. 检查消息是否已经被撤回
            if (MessageStatusEnum.RECALLED.getStatus().equals(originalMessage.getStatus())) {
                log.warn("消息已经被撤回: messageId={}", revokeMsg.getMessageId());
                return;
            }

            // 5. 验证撤回权限
            if (!hasRevokePermission(revokeMsg.getOperatingUserId(), originalMessage, revokeMsg.getConversationId())) {
                log.warn("用户无权撤回消息: operatingUserId={}, messageId={}, originalSenderId={}", 
                        revokeMsg.getOperatingUserId(), revokeMsg.getMessageId(), originalMessage.getUserId());
                return;
            }

            // 6. 更新消息状态为撤回,这里直接调用同步修改，避免异步落库失败导致消息还能被看到
            boolean updateSuccess = messageDubboService.updateMessageStatus(
                    revokeMsg.getMessageId(), MessageStatusEnum.RECALLED.getStatus());
            
            if (!updateSuccess) {
                log.error("更新消息状态失败: messageId={}", revokeMsg.getMessageId());
                return;
            }

            log.info("消息撤回成功: messageId={}, operatingUserId={}", 
                    revokeMsg.getMessageId(), revokeMsg.getOperatingUserId());

            // 7. 构建撤回消息通知DTO
            MessageDTO revokeNotificationDTO = MessageDTO.builder()
                    .messageId(originalMessage.getMessageId())
                    .syncId(originalMessage.getSyncId())
                    .conversationId(originalMessage.getConversationId())
                    .userId(originalMessage.getUserId()) // 设置为操作用户ID
                    .status(MessageStatusEnum.RECALLED.getStatus())
                    .messageType(originalMessage.getMessageType())
                    .replyMessage(originalMessage.getReplyMessage())
                    .content("[消息已撤回]" + revokeMsg.getOperatingUserId())
                    .updateTime(LocalDateTime.now())
                    .build();

            // 9. 推送撤回消息通知给会话中的所有成员
            pushRevokeNotificationToMembers(revokeMsg, revokeNotificationDTO);
            
        } catch (Exception e) {
            log.error("处理撤回消息失败: {}", message, e);
        }
    }

    /**
     * 检查用户是否有撤回消息的权限
     * 权限规则：
     * 1. 消息发送者可以撤回自己的消息
     * 2. 群管理员可以撤回群内任何消息
     * 3. 群主可以撤回群内任何消息
     */
    private boolean hasRevokePermission(Long operatingUserId, MessageDTO originalMessage, Long conversationId) {
        // 1. 如果是消息发送者，允许撤回
        if (operatingUserId.equals(originalMessage.getUserId())) {
            log.debug("消息发送者撤回自己的消息: operatingUserId={}, messageId={}", 
                    operatingUserId, originalMessage.getMessageId());
            return true;
        }

        // 2. 检查用户在会话中的角色权限
        Integer userRole = conversationDubboService.getUserRole(conversationId, operatingUserId);
        if (userRole == null) {
            log.warn("用户不是会话成员: userId={}, conversationId={}", operatingUserId, conversationId);
            return false;
        }

        // 3. 管理员(2)和群主(3)可以撤回群内任何消息
        if (userRole >= 2) {
            log.debug("管理员/群主撤回群内消息: operatingUserId={}, role={}, messageId={}", 
                    operatingUserId, userRole, originalMessage.getMessageId());
            return true;
        }

        log.debug("普通成员无权撤回他人消息: operatingUserId={}, role={}, messageId={}", 
                operatingUserId, userRole, originalMessage.getMessageId());
        return false;
    }

    /**
     * 推送撤回消息通知给会话中的所有成员
     */
    private void pushRevokeNotificationToMembers(RevokeMessageBody revokeMsg, MessageDTO revokeNotificationDTO) {
        try {
            // 获取会话成员列表
            List<Long> memberIds = conversationDubboService.getConversationMembers(revokeMsg.getConversationId())
                    .stream()
                    .map(member -> member.getUserId())
                    .toList();
            
            log.info("开始推送撤回消息通知给所有成员: conversationId={}, memberCount={}, messageId={}", 
                    revokeMsg.getConversationId(), memberIds.size(), revokeMsg.getMessageId());

            // 推送给所有群成员
            int successCount = 0;
            for (Long memberId : memberIds) {
                try {
                    boolean success = pushService.pushMessageToUser(revokeMsg.getAppId(), memberId, revokeNotificationDTO);
                    if (success) {
                        successCount++;
                    }
                    log.debug("推送撤回消息通知给成员: userId={}, messageId={}, success={}", 
                            memberId, revokeMsg.getMessageId(), success);
                } catch (Exception e) {
                    log.error("推送撤回消息通知给成员失败: userId={}, messageId={}, error={}", 
                            memberId, revokeMsg.getMessageId(), e.getMessage());
                }
            }
            
            log.info("撤回消息通知推送完成: conversationId={}, messageId={}, 总成员={}, 成功推送={}", 
                    revokeMsg.getConversationId(), revokeMsg.getMessageId(), memberIds.size(), successCount);
                    
        } catch (Exception e) {
            log.error("推送撤回消息通知失败: conversationId={}, messageId={}, error={}", 
                    revokeMsg.getConversationId(), revokeMsg.getMessageId(), e.getMessage(), e);
        }
    }

    @Override
    public int commandType() {
        return CommandType.REVOKE_MESSAGE.getType();
    }
}