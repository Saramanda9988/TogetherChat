package com.luna.chatserver.chat.service;

import com.luna.chatserver.chat.domain.entity.Message;
import com.luna.chatserver.chat.domain.request.message.ChatMessageDeleteRequest;
import com.luna.chatserver.chat.domain.request.message.ChatMessagePageRequest;
import com.luna.chatserver.chat.domain.request.message.ChatMessageRequest;
import com.luna.chatserver.chat.domain.request.message.ChatMessageUpdateRequest;
import com.luna.chatserver.common.domain.vo.response.CursorPageBaseResponse;

public interface ChatService {

    void sendMessage(ChatMessageRequest request, Long userId);

    void updateMessage(ChatMessageUpdateRequest request, Long userId);

    void deleteMessage(ChatMessageDeleteRequest request, Long userId);

    Message getMessageDetail(Long id, Long userId);

    CursorPageBaseResponse<Message> getMessageList(ChatMessagePageRequest request, Long userId);
}