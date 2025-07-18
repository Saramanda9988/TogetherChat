package com.luna.chatserver.chat.service.impl;


import com.luna.chatserver.chat.cache.IdCache;
import com.luna.chatserver.chat.dao.MessageDao;
import com.luna.chatserver.chat.domain.entity.Message;
import com.luna.chatserver.chat.domain.request.message.ChatMessageDeleteRequest;
import com.luna.chatserver.chat.domain.request.message.ChatMessagePageRequest;
import com.luna.chatserver.chat.domain.request.message.ChatMessageRequest;
import com.luna.chatserver.chat.domain.request.message.ChatMessageUpdateRequest;
import com.luna.chatserver.chat.domain.response.ChatMessageResponse;
import com.luna.chatserver.chat.enums.MessageErrorEnum;
import com.luna.chatserver.chat.enums.MessageStatusEnum;
import com.luna.chatserver.chat.event.producer.MessageService;
import com.luna.chatserver.chat.service.ChatService;
import com.luna.chatserver.chat.event.producer.PushService;
import com.luna.chatserver.chat.service.strategy.message.AbstractMessageHandler;
import com.luna.chatserver.chat.service.strategy.message.MessageHandlerFactory;
import com.luna.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.common.domain.vo.response.WSBaseResp;
import com.luna.common.enums.WSReqTypeEnum;
import com.luna.common.exception.BusinessException;
import com.luna.common.utils.AssertUtil;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@DubboService
public class ChatServiceImpl implements ChatService {

    private final IdCache idCache;

    private final PushService pushService;

    private final MessageService messageService;

    private final MessageDao messageDao;

    /**
     * 发送消息
     *
     * @param request
     * @param userId
     */
    @Override
    @Transactional
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
        send(message);
        messageService.savePushMsg(message);
    }

    public void send(Message message) {
        // 创建消息回复
        ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
        chatMessageResponse.setMessage(message);

        // FIXME: 这里需要从会话服务中操作
        // 获取对应群组中的用户id
        List<Long> userIdList = roomMemberDao.listUserIdByGroupId(message.getGroupId());

        // 构建WebsocketRep
        WSBaseResp<ChatMessageResponse> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSReqTypeEnum.MESSAGE.getType());
        wsBaseResp.setData(chatMessageResponse);

        // 推动新消息
        pushService.sendPushMsg(wsBaseResp, userIdList);
    }

    /**
     * 修改消息
     *
     * @param request
     * @param userId
     */
    @Override
    public void updateMessage(ChatMessageUpdateRequest request, Long userId) {
        AssertUtil.equal(request.getUserId(), userId, MessageErrorEnum.UPDATE_DENY.getErrorMsg());
        Message message = messageDao.getById(request.getMessageID());
        if (Objects.isNull(message)) {
            throw new BusinessException(MessageErrorEnum.NO_SUCH_MESSAGE);
        }
        // 只复制需要更新的字段，避免覆盖关键属性
        message.setContent(request.getContent());
        message.setExtra(request.getExtra());
        message.setSpecialEffects(request.getSpecialEffects());
        message.setStatus(MessageStatusEnum.MODIFY.getStatus());

        send(message);
        messageService.changePushMsg(message);
    }

    /**
     * 删除消息
     *
     * @param request
     * @param userId
     */
    @Override
    public void deleteMessage(ChatMessageDeleteRequest request, Long userId) {
        // TODO: 这里需要检查用户是否有权限删除消息
        // 查询当前用户群角色
        RoomMember member = roomMemberDao.getMemberByGroupIdAndUserId(request.getGroupId(), userId);
        AssertUtil.isNull(member, MessageErrorEnum.PERMISSION_DENY);

        // 检查是否有权限
        if (Objects.equals(member.getRole(), MemberTypeEnum.MEMBER.getType()) && !Objects.equals(request.getOwnerId(), userId)) {
            throw new BusinessException(MessageErrorEnum.DELETE_DENY);
        }

        // 检查消息是否存在
        Message message = messageDao.getById(request.getMessageID());
        AssertUtil.isNull(message, MessageErrorEnum.NO_SUCH_MESSAGE);

        message.setStatus(MessageStatusEnum.DELETE.getStatus());
        send(message);
        messageService.changePushMsg(message);
    }

    /**
     * 获取消息详情
     *
     * @param id
     * @param userId
     * @return
     */
    @Override
    public Message getMessageDetail(Long id, Long userId) {
        return messageDao.getById(id);
    }

    /**
     * 获取消息列表
     *
     * @param request
     * @param userId
     * @return
     */
    @Override
    public CursorPageBaseResponse<Message> getMessageList(ChatMessagePageRequest request, Long userId) {

        // 得到消息的游标列表
        CursorPageBaseResponse<Message> cursorPage = messageDao.getCursorPage(request.getGroupId(), request, Long.MAX_VALUE);

        // 如果不是空就转换
        return cursorPage.isEmpty()
                ? CursorPageBaseResponse.empty()
                : CursorPageBaseResponse.init(cursorPage, cursorPage.getList());
    }
}
