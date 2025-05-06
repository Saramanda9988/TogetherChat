package com.luna.togetherchat.chat.service.impl;

import com.luna.togetherchat.chat.cache.IdCache;
import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.chat.domain.request.message.ChatMessageDeleteRequest;
import com.luna.togetherchat.chat.domain.request.message.ChatMessagePageRequest;
import com.luna.togetherchat.chat.domain.request.message.ChatMessageRequest;
import com.luna.togetherchat.chat.domain.request.message.ChatMessageUpdateRequest;
import com.luna.togetherchat.chat.domain.response.ChatMessageResponse;
import com.luna.togetherchat.chat.enums.MessageTypeEnum;
import com.luna.togetherchat.chat.event.MessageSendEvent;
import com.luna.togetherchat.chat.service.ChatService;
import com.luna.togetherchat.chat.service.strategy.message.AbstractMessageHandler;
import com.luna.togetherchat.chat.service.strategy.message.MessageHandlerFactory;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.group.dao.GroupMemberDao;
import com.luna.togetherchat.websocket.domain.enums.WSRespTypeEnum;
import com.luna.togetherchat.websocket.domain.vo.request.WSBaseResp;
import com.luna.togetherchat.websocket.service.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final IdCache idCache;

    private final GroupMemberDao groupMemberDao;

    private final PushService pushService;

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 发送消息
     *
     * @param request
     * @param userId
     */
    @Override
    public void sendMessage(ChatMessageRequest request, Long userId) {
        // 获取对应的处理函数
        AbstractMessageHandler<?> messageHandler = MessageHandlerFactory.getStrategyNoNull(request.getMessageType());

        // 检查消息合理性，并且转换成数据库的消息格式
        Message message = messageHandler.checkAndConvert(request, userId);

        // 生成 sync id 用于标识消息的发送顺序， messageId用于标识全局唯一，position用于表示位置的顺序
        Long nextSyncId = idCache.getNextSyncId(request.getGroupId());
        message.setSyncId(nextSyncId);
        message.setMessageID(idCache.getNextMessageId());

        // 设置时间不然推送的时候没有时间
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());

        // 推送，然后异步落库
        sendAndSave(message);
    }

    public void sendAndSave(Message message) {
        // 创建消息回复
        ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
        chatMessageResponse.setMessage(message);

        // 获取对应群组中的用户id
        List<Long> userIdList = groupMemberDao.listUserIdByGroupId(message.getGroupId());

        // 构建WebsocketRep
        WSBaseResp<ChatMessageResponse> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MESSAGE.getType());
        wsBaseResp.setData(chatMessageResponse);

        // 推动新消息并落库
        pushService.sendPushMsg(wsBaseResp, userIdList);
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, message));
    }

    /**
     * 修改消息
     *
     * @param request
     * @param userId
     */
    @Override
    public void updateMessage(ChatMessageUpdateRequest request, Long userId) {

    }

    /**
     * 删除消息
     *
     * @param request
     * @param userId
     */
    @Override
    public void deleteMessage(ChatMessageDeleteRequest request, Long userId) {

    }

    /**
     * 获取消息详情
     *
     * @param id
     * @param userId
     * @return
     */
    @Override
    public ChatMessageResponse getMessageDetail(Long id, Long userId) {
        return null;
    }

    /**
     * 获取消息列表
     *
     * @param request
     * @param userId
     * @return
     */
    @Override
    public CursorPageBaseResponse<ChatMessageResponse> getMessageList(ChatMessagePageRequest request, Long userId) {
        return null;
    }
}
