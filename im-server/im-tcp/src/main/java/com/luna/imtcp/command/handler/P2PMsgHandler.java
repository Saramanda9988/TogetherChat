package com.luna.imtcp.command.handler;

import com.luna.common.constant.MQConstant;
import com.luna.common.utils.AssertUtil;
import com.luna.common.utils.JsonUtils;
import com.luna.common.utils.MQProducer;
import com.luna.common.utils.RedisUtils;
import com.luna.idserver.api.service.IdGenerator;
import com.luna.imtcp.api.constants.WebConstants;
import com.luna.imtcp.api.enums.CommandType;
import com.luna.imtcp.api.vo.WebMessage;
import com.luna.imtcp.api.vo.msgBody.MessageVOBody;
import com.luna.imtcp.api.vo.msgBody.P2PMessageBody;
import com.luna.imtcp.command.CommandHandler;
import com.luna.imtcp.service.PushService;
import com.luna.messageserver.api.dto.MessageDTO;
import com.luna.messageserver.api.enums.MessageStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class P2PMsgHandler implements CommandHandler {
    
    @DubboReference
    private IdGenerator idGenerator;
    
    private final MQProducer mqProducer;
    private final PushService pushService;

    @Override
    public void handle(WebMessage message) {
        log.info("P2PMsgHandler收到消息: {}", message);
        
        try {
            // 1. 解析消息体
            P2PMessageBody p2pMsg = JsonUtils.toObj(message.getMessagePack(), P2PMessageBody.class);
            if (p2pMsg == null) {
                log.error("消息体解析失败: {}", message.getMessagePack());
                return;
            }

            // 2. 生成消息ID和同步ID
            Long messageId = idGenerator.generateMessageId();
            Integer syncId = idGenerator.generateConversationSyncId(p2pMsg.getConversationId()).intValue();
            
            // 3. 构建消息DTO
            MessageDTO messageDTO = MessageDTO.builder()
                    .messageId(messageId)
                    .syncId(syncId)
                    .conversationId(p2pMsg.getConversationId())
                    .userId(p2pMsg.getFromId())
                    .status(MessageStatusEnum.NORMAL.getStatus()) // 正常状态
                    .messageType(p2pMsg.getMessageType()) // 文本消息
                    .replyMessage(p2pMsg.getReplyMessage())
                    .content(p2pMsg.getContent())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            // 4. 先落库到Redis缓存（快速响应）
            String cacheKey = p2pMsg.getAppId() + WebConstants.CacheMessage + messageId;
            RedisUtils.set(cacheKey, JsonUtils.toStr(messageDTO), 3600); // 缓存1小时
            log.info("消息已缓存到Redis: messageId={}, cacheKey={}", messageId, cacheKey);

            // 5. 发布消息到MQ进行异步落库
            mqProducer.saveMsg(MQConstant.MESSAGE_SAVE_EXCHANGE, messageDTO);
            log.info("消息已发送到MQ进行异步落库: messageId={}", messageId);

            // 6. 验证必要参数
            AssertUtil.isNotNull(p2pMsg.getFromId(), "发送者ID不能为空");
            AssertUtil.isNotNull(p2pMsg.getToId(), "接收者ID不能为空");

            // 8. 使用推送服务推送给接收者
            boolean receiverSuccess = pushService.pushMessageToUser(p2pMsg.getAppId(), p2pMsg.getToId(), messageDTO);
            log.info("推送给接收者结果: toId={}, messageId={}, success={}", p2pMsg.getToId(), messageId, receiverSuccess);

            // 9. 推送给发送者确认消息已发送
            boolean senderSuccess = pushService.pushMessageToUser(p2pMsg.getAppId(), p2pMsg.getFromId(), messageDTO);
            log.info("推送给发送者结果: fromId={}, messageId={}, success={}", p2pMsg.getFromId(), messageId, senderSuccess);
            
        } catch (Exception e) {
            log.error("处理P2P消息失败: {}", message, e);
        }
    }

    @Override
    public int commandType() {
        return CommandType.SINGLE_MESSAGE.getType();
    }
}
