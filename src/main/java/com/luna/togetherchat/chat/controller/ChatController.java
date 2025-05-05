package com.luna.togetherchat.chat.controller;

import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.chat.domain.request.message.ChatMessagePageRequest;
import com.luna.togetherchat.chat.domain.request.message.ChatMessageRequest;
import com.luna.togetherchat.chat.domain.request.message.InsertMessageRequest;
import com.luna.togetherchat.chat.domain.request.message.MoveMessageRequest;
import com.luna.togetherchat.chat.domain.response.ChatMessageResponse;
import com.luna.togetherchat.chat.service.ChatService;
import com.luna.togetherchat.common.domain.vo.response.ApiResult;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.common.enums.BusinessErrorEnum;
import com.luna.togetherchat.common.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
