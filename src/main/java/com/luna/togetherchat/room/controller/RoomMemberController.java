package com.luna.togetherchat.room.controller;

import com.luna.togetherchat.common.domain.vo.response.ApiResult;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.common.utils.RequestHolder;
import com.luna.togetherchat.room.domain.entity.RoomMember;
import com.luna.togetherchat.room.domain.request.RoomMemberAddRequest;
import com.luna.togetherchat.room.domain.request.RoomMemberPageRequest;
import com.luna.togetherchat.room.domain.request.RoomMemberRemoveRequest;
import com.luna.togetherchat.room.domain.request.RoomMemberUpdateRequest;
import com.luna.togetherchat.room.service.RoomMemberService;
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
public class RoomMemberController {

    private final RoomMemberService roomMemberService;

    /**
     * 1.添加成员
     */
    @PostMapping()
    @Operation(summary = "添加群组成员")
    public ApiResult<Void> addGroupMembers(@RequestBody @Valid RoomMemberAddRequest request) {
        Long userId = RequestHolder.get().getUserId();
        roomMemberService.addRoomMembers(request, userId);
        return ApiResult.success();
    }

//    /**
//     * 2.查看用户详情（user信息）
//     */
//    @GetMapping("/{groupId}/member/{userId}")
//    @Operation(summary = "获取群组成员详情")
//    public ApiResult<GroupMemberResponse> getMemberDetail(
//            @PathVariable Long groupId,
//            @PathVariable Long userId) {
//        Long operatorId = RequestHolder.get().getUserId();
//        return ApiResult.success(groupMemberService.getMemberDetail(groupId, userId, operatorId));
//    }

    /**
     * 3.翻页查看所有群员
     */
    @GetMapping("/page")
    @Operation(summary = "分页获取群组成员列表")
    public ApiResult<CursorPageBaseResponse<RoomMember>> getGroupMembers(@Valid RoomMemberPageRequest request) {
        Long userId = RequestHolder.get().getUserId();
        return ApiResult.success(roomMemberService.getRoomMembers(request, userId));
    }

    /**
     * 4.踢人
     */
    @DeleteMapping()
    @Operation(summary = "移除群组成员")
    public ApiResult<Void> removeGroupMember(@RequestBody @Valid RoomMemberRemoveRequest request) {
        Long userId = RequestHolder.get().getUserId();
        roomMemberService.removeRoomMember(request, userId);
        return ApiResult.success();
    }

    /**
     * 5.更新成员身份（给管理员）
     */
    @PutMapping()
    @Operation(summary = "更新群组成员角色")
    public ApiResult<Void> updateMemberRole(@RequestBody @Valid RoomMemberUpdateRequest request) {
        Long userId = RequestHolder.get().getUserId();
        roomMemberService.updateMemberRole(request, userId);
        return ApiResult.success();
    }
}
