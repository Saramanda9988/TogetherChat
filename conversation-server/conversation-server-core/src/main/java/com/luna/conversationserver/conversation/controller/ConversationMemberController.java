package com.luna.conversationserver.conversation.controller;

import com.luna.common.domain.vo.response.ApiResult;
import com.luna.common.utils.RequestHolder;
import com.luna.conversationserver.conversation.domain.request.AddMemberRequest;
import com.luna.conversationserver.conversation.domain.request.BatchAddMemberRequest;
import com.luna.conversationserver.conversation.domain.request.MuteMemberRequest;
import com.luna.conversationserver.conversation.domain.request.UpdateMemberRoleRequest;
import com.luna.conversationserver.conversation.domain.response.ConversationMemberResponse;
import com.luna.conversationserver.conversation.service.ConversationMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  会话成员控制器
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-10-13
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "ConversationMemberController", description = "会话成员相关接口")
@RequestMapping("/conversation/{conversationId}/member")
public class ConversationMemberController {

    private final ConversationMemberService conversationMemberService;

    /**
     * 添加成员
     */
    @PostMapping
    @Operation(summary = "添加成员", description = "邀请用户入群")
    public ApiResult<ConversationMemberResponse> addMember(
            @PathVariable Long conversationId,
            @RequestBody @Valid AddMemberRequest request) {
        Long userId = RequestHolder.get().getUserId();
        request.setConversationId(conversationId); // 设置从路径参数获取的conversationId
        ConversationMemberResponse response = conversationMemberService.addMember(request, userId);
        return ApiResult.success(response);
    }

    /**
     * 批量添加成员
     */
    @PostMapping("/batch")
    @Operation(summary = "批量添加成员", description = "批量邀请用户入群")
    public ApiResult<List<ConversationMemberResponse>> batchAddMembers(
            @PathVariable Long conversationId,
            @RequestBody @Valid BatchAddMemberRequest request) {
        Long userId = RequestHolder.get().getUserId();
        request.setConversationId(conversationId); // 设置从路径参数获取的conversationId
        List<ConversationMemberResponse> responses = conversationMemberService.batchAddMembers(request, userId);
        return ApiResult.success(responses);
    }

    /**
     * 移除成员
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "移除成员", description = "移除指定成员")
    public ApiResult<Void> removeMember(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {
        Long currentUserId = RequestHolder.get().getUserId();
        conversationMemberService.removeMember(conversationId, userId, currentUserId);
        return ApiResult.success();
    }

    /**
     * 获取成员列表
     */
    @GetMapping
    @Operation(summary = "获取成员列表", description = "查询群成员列表")
    public ApiResult<List<ConversationMemberResponse>> getMembers(@PathVariable Long conversationId) {
        Long userId = RequestHolder.get().getUserId();
        List<ConversationMemberResponse> responses = conversationMemberService.getMembers(conversationId, userId);
        return ApiResult.success(responses);
    }

    /**
     * 退出群聊
     */
    @PostMapping("/quit")
    @Operation(summary = "退出群聊", description = "当前用户退出群聊")
    public ApiResult<Void> quitConversation(@PathVariable Long conversationId) {
        Long userId = RequestHolder.get().getUserId();
        conversationMemberService.quitConversation(conversationId, userId);
        return ApiResult.success();
    }

    /**
     * 修改成员角色
     */
    @PutMapping("/{userId}/role")
    @Operation(summary = "修改成员角色", description = "修改成员角色（管理员、普通成员）")
    public ApiResult<ConversationMemberResponse> updateMemberRole(
            @PathVariable Long conversationId,
            @PathVariable Long userId,
            @RequestBody @Valid UpdateMemberRoleRequest request) {
        Long currentUserId = RequestHolder.get().getUserId();
        ConversationMemberResponse response = conversationMemberService.updateMemberRole(conversationId, userId, request, currentUserId);
        return ApiResult.success(response);
    }

    /**
     * 禁言成员
     */
    @PutMapping("/{userId}/mute")
    @Operation(summary = "禁言成员", description = "设置或解除禁言")
    public ApiResult<ConversationMemberResponse> muteMember(
            @PathVariable Long conversationId,
            @PathVariable Long userId,
            @RequestBody @Valid MuteMemberRequest request) {
        Long currentUserId = RequestHolder.get().getUserId();
        ConversationMemberResponse response = conversationMemberService.muteMember(conversationId, userId, request, currentUserId);
        return ApiResult.success(response);
    }
}
