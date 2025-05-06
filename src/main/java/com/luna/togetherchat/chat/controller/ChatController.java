package com.luna.togetherchat.chat.controller;

import com.luna.togetherchat.chat.domain.request.message.ChatMessageDeleteRequest;
import com.luna.togetherchat.chat.domain.request.message.ChatMessageRequest;
import com.luna.togetherchat.chat.domain.request.message.ChatMessageUpdateRequest;
import com.luna.togetherchat.chat.service.ChatService;
import com.luna.togetherchat.common.domain.vo.response.ApiResult;
import com.luna.togetherchat.common.utils.RequestHolder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 消息处理相关的页表
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "ChatController", description = "消息收发，消息标记，与消息相关的基本在这")
@RequestMapping("/capi/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/message")
    public ApiResult<Void> sendMessage(@RequestBody ChatMessageRequest request) {
        Long userId = RequestHolder.get().getUserId();
        chatService.sendMessage(request, userId);
        return ApiResult.success();
    }

    @PutMapping("/message")
    public ApiResult<Void> updateMessage(@RequestBody ChatMessageUpdateRequest request) {
        Long userId = RequestHolder.get().getUserId();
        chatService.updateMessage(request, userId);
        return ApiResult.success();
    }

    @DeleteMapping("/message")
    public ApiResult<Void> deleteMessage(@RequestBody ChatMessageDeleteRequest request) {
        Long userId = RequestHolder.get().getUserId();
        chatService.deleteMessage(request, userId);
        return ApiResult.success();
    }
}
