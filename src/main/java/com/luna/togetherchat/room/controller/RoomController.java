package com.luna.togetherchat.room.controller;

import com.luna.togetherchat.common.domain.vo.response.ApiResult;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.common.utils.RequestHolder;
import com.luna.togetherchat.room.domain.request.RoomCreateRequest;
import com.luna.togetherchat.room.domain.request.RoomPageRequest;
import com.luna.togetherchat.room.domain.request.RoomUpdateRequest;
import com.luna.togetherchat.room.domain.response.RoomResponse;
import com.luna.togetherchat.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  群组控制器
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "GroupController", description = "群组相关接口")
@RequestMapping("/capi/group")
public class RoomController {

    private final RoomService roomService;

    /**
     * 1.创建一个群组(前端决定是单聊还是群聊)
     */
    @PostMapping
    @Operation(summary = "创建群组")
    public ApiResult<RoomResponse> createGroup(@RequestBody @Valid RoomCreateRequest request) {
        Long userId = RequestHolder.get().getUserId();
        return ApiResult.success(roomService.createRoom(request, userId));
    }

    /**
     * 2.获取群聊信息
     */
    @GetMapping("/{groupId}")
    @Operation(summary = "获取群组信息")
    public ApiResult<RoomResponse> getGroupInfo(@PathVariable Long groupId) {
        Long userId = RequestHolder.get().getUserId();
        return ApiResult.success(roomService.getRoomInfo(groupId, userId));
    }

    /**
     * 3.删除一个群组
     */
    @DeleteMapping("/{groupId}")
    @Operation(summary = "删除群组")
    public ApiResult<Void> deleteGroup(@PathVariable Long groupId) {
        Long userId = RequestHolder.get().getUserId();
        roomService.deleteRoom(groupId, userId);
        return ApiResult.success();
    }

    /**
     * 4.修改群组信息
     */
    @PutMapping
    @Operation(summary = "更新群组信息")
    public ApiResult<RoomResponse> updateGroup(@RequestBody @Valid RoomUpdateRequest request) {
        Long userId = RequestHolder.get().getUserId();
        return ApiResult.success(roomService.updateRoom(request, userId));
    }

    /**
     * 5.分页获取群组信息（游标）
     */
    @GetMapping("/page")
    @Operation(summary = "分页获取群组列表")
    public ApiResult<CursorPageBaseResponse<RoomResponse>> getUserGroups(@Valid RoomPageRequest request) {
        Long userId = RequestHolder.get().getUserId();
        return ApiResult.success(roomService.getUserRooms(request, userId));
    }
}
