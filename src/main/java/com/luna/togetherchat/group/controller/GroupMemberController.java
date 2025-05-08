package com.luna.togetherchat.group.controller;

import com.luna.togetherchat.common.domain.vo.response.ApiResult;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.common.utils.RequestHolder;
import com.luna.togetherchat.group.domain.entity.GroupMember;
import com.luna.togetherchat.group.domain.request.GroupMemberAddRequest;
import com.luna.togetherchat.group.domain.request.GroupMemberPageRequest;
import com.luna.togetherchat.group.domain.request.GroupMemberRemoveRequest;
import com.luna.togetherchat.group.domain.request.GroupMemberUpdateRequest;
import com.luna.togetherchat.group.domain.response.GroupMemberResponse;
import com.luna.togetherchat.group.service.GroupMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  群组成员控制器
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "GroupMemberController", description = "群组成员相关接口")
@RequestMapping("/capi/groupMember")
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    /**
     * 1.添加成员
     */
    @PostMapping()
    @Operation(summary = "添加群组成员")
    public ApiResult<Void> addGroupMembers(@RequestBody @Valid GroupMemberAddRequest request) {
        Long userId = RequestHolder.get().getUserId();
        groupMemberService.addGroupMembers(request, userId);
        return ApiResult.success();
    }

    /**
     * 2.查看用户详情（user信息）
     */
    @GetMapping("/{groupId}/member/{userId}")
    @Operation(summary = "获取群组成员详情")
    public ApiResult<GroupMemberResponse> getMemberDetail(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        Long operatorId = RequestHolder.get().getUserId();
        return ApiResult.success(groupMemberService.getMemberDetail(groupId, userId, operatorId));
    }

    /**
     * 3.翻页查看所有群员
     */
    @GetMapping("/page")
    @Operation(summary = "分页获取群组成员列表")
    public ApiResult<CursorPageBaseResponse<GroupMember>> getGroupMembers(@Valid GroupMemberPageRequest request) {
        Long userId = RequestHolder.get().getUserId();
        return ApiResult.success(groupMemberService.getGroupMembers(request, userId));
    }

    /**
     * 4.踢人
     */
    @DeleteMapping()
    @Operation(summary = "移除群组成员")
    public ApiResult<Void> removeGroupMember(@RequestBody @Valid GroupMemberRemoveRequest request) {
        Long userId = RequestHolder.get().getUserId();
        groupMemberService.removeGroupMember(request, userId);
        return ApiResult.success();
    }

    /**
     * 5.更新成员身份（给管理员）
     */
    @PutMapping()
    @Operation(summary = "更新群组成员角色")
    public ApiResult<Void> updateMemberRole(@RequestBody @Valid GroupMemberUpdateRequest request) {
        Long userId = RequestHolder.get().getUserId();
        groupMemberService.updateMemberRole(request, userId);
        return ApiResult.success();
    }
}
