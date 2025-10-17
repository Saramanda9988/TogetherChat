package com.luna.imtcp.command.handler;

import com.luna.common.constant.MQConstant;
import com.luna.common.utils.AssertUtil;
import com.luna.common.utils.JsonUtils;
import com.luna.common.utils.MQProducer;
import com.luna.common.utils.RedisUtils;
import com.luna.conversationserver.api.service.ConversationDubboService;
import com.luna.idserver.api.service.IdGenerator;
import com.luna.imtcp.api.constants.WebConstants;
import com.luna.imtcp.api.enums.CommandType;
import com.luna.imtcp.api.vo.WebMessage;
import com.luna.imtcp.api.vo.msgBody.GroupMessageBody;
import com.luna.imtcp.command.CommandHandler;
import com.luna.imtcp.service.PushService;
import com.luna.messageserver.api.dto.MessageDTO;
import com.luna.messageserver.api.enums.MessageStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupMsgHandler implements CommandHandler {
    
    @DubboReference
    private IdGenerator idGenerator;
    
    @DubboReference(group = "conversation-service")
    private ConversationDubboService conversationDubboService;
    
    private final PushService pushService;
    private final MQProducer mqProducer;

    @Override
    public void handle(WebMessage message) {
        log.info("GroupMsgHandler收到群组消息: {}", message);
        
        try {
            // 1. 解析消息体
            GroupMessageBody groupMsg = JsonUtils.toObj(message.getMessagePack(), GroupMessageBody.class);
            if (groupMsg == null) {
                log.error("群组消息体解析失败: {}", message.getMessagePack());
                return;
            }

            // 2. 验证必要参数
            AssertUtil.isNotNull(groupMsg.getFromId(), "发送者ID不能为空");
            AssertUtil.isNotNull(groupMsg.getConversationId(), "会话ID不能为空");

            // 3. 验证用户是否是群成员
            boolean isMember = conversationDubboService.isMember(groupMsg.getConversationId(), groupMsg.getFromId());
            if (!isMember) {
                log.warn("用户不是群成员，拒绝发送消息: userId={}, conversationId={}", 
                        groupMsg.getFromId(), groupMsg.getConversationId());
                return;
            }

            // 4. 生成消息ID和同步ID
            Long messageId = idGenerator.generateMessageId();
            Integer syncId = idGenerator.generateConversationSyncId(groupMsg.getConversationId()).intValue();
            
            // 5. 构建消息DTO
            MessageDTO messageDTO = MessageDTO.builder()
                    .messageId(messageId)
                    .syncId(syncId)
                    .conversationId(groupMsg.getConversationId())
                    .userId(groupMsg.getFromId())
                    .status(MessageStatusEnum.NORMAL.getStatus()) // 正常状态
                    .messageType(groupMsg.getMessageType())
                    .replyMessage(groupMsg.getReplyMessage())
                    .content(groupMsg.getContent())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            // 6. 先落库到Redis缓存（快速响应）
            String cacheKey = groupMsg.getAppId() + WebConstants.CacheMessage + messageId;
            RedisUtils.set(cacheKey, JsonUtils.toStr(messageDTO), 3600); // 缓存1小时
            log.info("群组消息已缓存到Redis: messageId={}, cacheKey={}", messageId, cacheKey);

            // 7. 发布消息到MQ进行异步落库
            mqProducer.saveMsg(MQConstant.MESSAGE_SAVE_EXCHANGE, messageDTO);
            log.info("群组消息已发送到MQ进行异步落库: messageId={}", messageId);

            // 8. 获取群组成员列表并推送消息
            List<Long> memberIds = conversationDubboService.getConversationMembers(groupMsg.getConversationId())
                    .stream()
                    .map(member -> member.getUserId())
                    .toList();
            
            log.info("开始推送群组消息给所有成员: conversationId={}, memberCount={}, messageId={}", 
                    groupMsg.getConversationId(), memberIds.size(), messageId);

            // 9. 并发推送消息给所有群成员
            int successCount = 0;
            for (Long memberId : memberIds) {
                try {
                    boolean success = pushService.pushMessageToUser(groupMsg.getAppId(), memberId, messageDTO);
                    if (success) {
                        successCount++;
                    }
                    log.debug("推送群组消息给成员: userId={}, messageId={}, success={}", 
                            memberId, messageId, success);
                } catch (Exception e) {
                    log.error("推送群组消息给成员失败: userId={}, messageId={}, error={}", 
                            memberId, messageId, e.getMessage());
                }
            }
            
            log.info("群组消息推送完成: conversationId={}, messageId={}, 总成员={}, 成功推送={}", 
                    groupMsg.getConversationId(), messageId, memberIds.size(), successCount);
            
        } catch (Exception e) {
            log.error("处理群组消息失败: {}", message, e);
        }
    }

    @Override
    public int commandType() {
        return CommandType.GROUP_MESSAGE.getType();
    }
}
