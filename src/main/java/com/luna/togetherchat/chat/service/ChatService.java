package com.luna.togetherchat.chat.service;

import com.luna.togetherchat.chat.domain.request.message.ChatMessageDeleteRequest;
import com.luna.togetherchat.chat.domain.request.message.ChatMessagePageRequest;
import com.luna.togetherchat.chat.domain.request.message.ChatMessageRequest;
import com.luna.togetherchat.chat.domain.request.message.ChatMessageUpdateRequest;
import com.luna.togetherchat.chat.domain.response.ChatMessageResponse;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;

public interface ChatService {

    void sendMessage(ChatMessageRequest request, Long userId);

    void updateMessage(ChatMessageUpdateRequest request, Long userId);

    void deleteMessage(ChatMessageDeleteRequest request, Long userId);

    ChatMessageResponse getMessageDetail(Long id, Long userId);

    CursorPageBaseResponse<ChatMessageResponse> getMessageList(ChatMessagePageRequest request, Long userId);
}