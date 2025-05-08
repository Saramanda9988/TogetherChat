package com.luna.togetherchat.group.service;

import com.luna.togetherchat.common.domain.vo.response.ApiResult;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.group.domain.entity.GroupMember;
import com.luna.togetherchat.group.domain.request.GroupMemberAddRequest;
import com.luna.togetherchat.group.domain.request.GroupMemberPageRequest;
import com.luna.togetherchat.group.domain.request.GroupMemberRemoveRequest;
import com.luna.togetherchat.group.domain.request.GroupMemberUpdateRequest;
import com.luna.togetherchat.group.domain.response.GroupMemberResponse;

/**
 * <p>
 *  群组成员服务接口
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
public interface GroupMemberService {

    /**
     * 添加群组成员
     *
     * @param request 添加成员请求
     * @param operatorId 操作者用户ID
     */
    void addGroupMembers(GroupMemberAddRequest request, Long operatorId);

    /**
     * 获取群组成员详情
     *
     * @param groupId 群组ID
     * @param userId 用户ID
     * @param operatorId 操作者用户ID
     * @return 成员详情
     */
    GroupMemberResponse getMemberDetail(Long groupId, Long userId, Long operatorId);

    /**
     * 分页获取群组成员列表
     *
     * @param request 分页请求
     * @param operatorId 操作者用户ID
     * @return 成员分页列表
     */
    CursorPageBaseResponse<GroupMember> getGroupMembers(GroupMemberPageRequest request, Long operatorId);

    /**
     * 移除群组成员
     *
     * @param request 移除成员请求
     * @param operatorId 操作者用户ID
     */
    void removeGroupMember(GroupMemberRemoveRequest request, Long operatorId);

    /**
     * 更新群组成员角色
     *
     * @param request 更新成员角色请求
     * @param operatorId 操作者用户ID
     */
    void updateMemberRole(GroupMemberUpdateRequest request, Long operatorId);
}
