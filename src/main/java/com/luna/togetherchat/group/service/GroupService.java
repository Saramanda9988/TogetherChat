package com.luna.togetherchat.group.service;

import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.group.domain.request.GroupCreateRequest;
import com.luna.togetherchat.group.domain.request.GroupPageRequest;
import com.luna.togetherchat.group.domain.request.GroupUpdateRequest;
import com.luna.togetherchat.group.domain.response.GroupResponse;

/**
 * <p>
 *  群组服务接口
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
public interface GroupService {

    /**
     * 创建群组
     *
     * @param request 创建群组请求
     * @param userId 创建者用户ID
     * @return 群组信息
     */
    GroupResponse createGroup(GroupCreateRequest request, Long userId);

    /**
     * 获取群组信息
     *
     * @param groupId 群组ID
     * @param userId 当前用户ID
     * @return 群组信息
     */
    GroupResponse getGroupInfo(Long groupId, Long userId);

    /**
     * 删除群组
     *
     * @param groupId 群组ID
     * @param userId 当前用户ID
     */
    void deleteGroup(Long groupId, Long userId);

    /**
     * 更新群组信息
     *
     * @param request 更新群组请求
     * @param userId 当前用户ID
     * @return 更新后的群组信息
     */
    GroupResponse updateGroup(GroupUpdateRequest request, Long userId);

    /**
     * 分页获取用户的群组列表
     *
     * @param request 分页请求
     * @param userId 用户ID
     * @return 群组分页列表
     */
    CursorPageBaseResponse<GroupResponse> getUserGroups(GroupPageRequest request, Long userId);
}
