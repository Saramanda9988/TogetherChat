package com.luna.togetherchat.group.controller;

import com.luna.togetherchat.common.domain.vo.response.ApiResult;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.common.utils.RequestHolder;
import com.luna.togetherchat.group.domain.request.GroupCreateRequest;
import com.luna.togetherchat.group.domain.request.GroupPageRequest;
import com.luna.togetherchat.group.domain.request.GroupUpdateRequest;
import com.luna.togetherchat.group.domain.response.GroupResponse;
import com.luna.togetherchat.group.service.GroupService;
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
public class GroupController {

    private final GroupService groupService;

    /**
     * 1.创建一个群组(前端决定是单聊还是群聊)
     */
    @PostMapping
    @Operation(summary = "创建群组")
    public ApiResult<GroupResponse> createGroup(@RequestBody @Valid GroupCreateRequest request) {
        Long userId = RequestHolder.get().getUserId();
        return ApiResult.success(groupService.createGroup(request, userId));
    }

    /**
     * 2.获取群聊信息
     */
    @GetMapping("/{groupId}")
    @Operation(summary = "获取群组信息")
    public ApiResult<GroupResponse> getGroupInfo(@PathVariable Long groupId) {
        Long userId = RequestHolder.get().getUserId();
        return ApiResult.success(groupService.getGroupInfo(groupId, userId));
    }

    /**
     * 3.删除一个群组
     */
    @DeleteMapping("/{groupId}")
    @Operation(summary = "删除群组")
    public ApiResult<Void> deleteGroup(@PathVariable Long groupId) {
        Long userId = RequestHolder.get().getUserId();
        groupService.deleteGroup(groupId, userId);
        return ApiResult.success();
    }

    /**
     * 4.修改群组信息
     */
    @PutMapping
    @Operation(summary = "更新群组信息")
    public ApiResult<GroupResponse> updateGroup(@RequestBody @Valid GroupUpdateRequest request) {
        Long userId = RequestHolder.get().getUserId();
        return ApiResult.success(groupService.updateGroup(request, userId));
    }

    /**
     * 5.分页获取群组信息（游标）
     */
    @GetMapping("/page")
    @Operation(summary = "分页获取群组列表")
    public ApiResult<CursorPageBaseResponse<GroupResponse>> getUserGroups(@Valid GroupPageRequest request) {
        Long userId = RequestHolder.get().getUserId();
        return ApiResult.success(groupService.getUserGroups(request, userId));
    }
}
