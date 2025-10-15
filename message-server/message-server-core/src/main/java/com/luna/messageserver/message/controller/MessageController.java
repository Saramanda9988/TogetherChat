package com.luna.messageserver.message.controller;

import com.luna.common.domain.vo.response.ApiResult;
import com.luna.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.common.utils.RequestHolder;
import com.luna.messageserver.message.domain.request.MessageHistoryRequest;
import com.luna.messageserver.message.domain.response.MessageResponse;
import com.luna.messageserver.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "MessageController", description = "消息相关接口")
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    /**
     * 获取会话历史消息
     */
    @GetMapping("/conversation/{id}")
    @Operation(summary = "获取历史消息", description = "获取指定会话的历史消息")
    public ApiResult<CursorPageBaseResponse<MessageResponse>> getHistoryMessages(
            @PathVariable("id") Long conversationId,
            @Valid MessageHistoryRequest request) {
        
        Long userId = RequestHolder.get().getUserId();
        log.info("用户 {} 获取会话 {} 的历史消息", userId, conversationId);
        
        CursorPageBaseResponse<MessageResponse> response = messageService.getHistoryMessages(conversationId, request);
        return ApiResult.success(response);
    }

    /**
     * 获取单条消息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取单条消息", description = "根据消息ID获取消息详情")
    public ApiResult<MessageResponse> getMessageById(@PathVariable("id") Long messageId) {
        
        Long userId = RequestHolder.get().getUserId();
        log.info("用户 {} 获取消息 {}", userId, messageId);
        
        MessageResponse response = messageService.getMessageById(messageId);
        return ApiResult.success(response);
    }
}
