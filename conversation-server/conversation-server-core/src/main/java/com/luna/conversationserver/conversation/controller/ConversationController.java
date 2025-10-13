package com.luna.conversationserver.conversation.controller;

import com.luna.common.domain.vo.response.ApiResult;
import com.luna.common.utils.RequestHolder;
import com.luna.conversationserver.conversation.domain.request.CreateGroupRequest;
import com.luna.conversationserver.conversation.domain.request.CreateP2PRequest;
import com.luna.conversationserver.conversation.domain.request.UpdateConversationRequest;
import com.luna.conversationserver.conversation.domain.response.ConversationResponse;
import com.luna.conversationserver.conversation.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  会话控制器
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-10-13
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "ConversationController", description = "会话相关接口")
@RequestMapping("/conversation")
public class ConversationController {

    private final ConversationService conversationService;

    /**
     * 创建或获取单聊会话
     */
    @PostMapping("/p2p")
    @Operation(summary = "创建单聊会话", description = "创建或获取两人的单聊会话")
    public ApiResult<ConversationResponse> createP2P(@RequestBody @Valid CreateP2PRequest request) {
        Long userId = RequestHolder.get().getUserId();
        ConversationResponse response = conversationService.createOrGetP2P(request, userId);
        return ApiResult.success(response);
    }

    /**
     * 创建群聊
     */
    @PostMapping("/group")
    @Operation(summary = "创建群聊", description = "创建一个新的群聊")
    public ApiResult<ConversationResponse> createGroup(@RequestBody @Valid CreateGroupRequest request) {
        Long userId = RequestHolder.get().getUserId();
        ConversationResponse response = conversationService.createGroup(request, userId);
        return ApiResult.success(response);
    }

    /**
     * 获取会话详情
     */
    @GetMapping("/{conversationId}")
    @Operation(summary = "获取会话详情", description = "获取会话详情（包括类型、成员数等）")
    public ApiResult<ConversationResponse> getConversationInfo(@PathVariable Long conversationId) {
        Long userId = RequestHolder.get().getUserId();
        ConversationResponse response = conversationService.getConversationInfo(conversationId, userId);
        return ApiResult.success(response);
    }

    /**
     * 查询我的所有会话
     */
    @GetMapping("/my")
    @Operation(summary = "查询我的所有会话", description = "查询当前用户的所有会话列表")
    public ApiResult<List<ConversationResponse>> getMyConversations() {
        Long userId = RequestHolder.get().getUserId();
        List<ConversationResponse> responses = conversationService.getUserConversations(userId);
        return ApiResult.success(responses);
    }

    /**
     * 更新群聊信息
     */
    @PutMapping("/{conversationId}")
    @Operation(summary = "更新群聊信息", description = "修改群聊名称、头像等信息")
    public ApiResult<ConversationResponse> updateConversation(
            @PathVariable Long conversationId,
            @RequestBody @Valid UpdateConversationRequest request) {
        Long userId = RequestHolder.get().getUserId();
        ConversationResponse response = conversationService.updateConversation(conversationId, request, userId);
        return ApiResult.success(response);
    }

    /**
     * 解散群聊
     */
    @DeleteMapping("/{conversationId}")
    @Operation(summary = "解散群聊", description = "解散群聊")
    public ApiResult<Void> deleteConversation(@PathVariable Long conversationId) {
        Long userId = RequestHolder.get().getUserId();
        conversationService.deleteConversation(conversationId, userId);
        return ApiResult.success();
    }
}
