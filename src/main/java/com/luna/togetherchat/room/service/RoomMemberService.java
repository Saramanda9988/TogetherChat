package com.luna.togetherchat.room.service;

import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.room.domain.entity.RoomMember;
import com.luna.togetherchat.room.domain.request.RoomMemberAddRequest;
import com.luna.togetherchat.room.domain.request.RoomMemberPageRequest;
import com.luna.togetherchat.room.domain.request.RoomMemberRemoveRequest;
import com.luna.togetherchat.room.domain.request.RoomMemberUpdateRequest;
import com.luna.togetherchat.room.domain.response.RoomMemberResponse;

/**
 * <p>
 *  群组成员服务接口
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
public interface RoomMemberService {

    /**
     * 添加群组成员
     *
     * @param request 添加成员请求
     * @param operatorId 操作者用户ID
     */
    void addRoomMembers(RoomMemberAddRequest request, Long operatorId);

    /**
     * 获取群组成员详情
     *
     * @param groupId 群组ID
     * @param userId 用户ID
     * @param operatorId 操作者用户ID
     * @return 成员详情
     */
    RoomMemberResponse getMemberDetail(Long groupId, Long userId, Long operatorId);

    /**
     * 分页获取群组成员列表
     *
     * @param request 分页请求
     * @param operatorId 操作者用户ID
     * @return 成员分页列表
     */
    CursorPageBaseResponse<RoomMember> getRoomMembers(RoomMemberPageRequest request, Long operatorId);

    /**
     * 移除群组成员
     *
     * @param request 移除成员请求
     * @param operatorId 操作者用户ID
     */
    void removeRoomMember(RoomMemberRemoveRequest request, Long operatorId);

    /**
     * 更新群组成员角色
     *
     * @param request 更新成员角色请求
     * @param operatorId 操作者用户ID
     */
    void updateMemberRole(RoomMemberUpdateRequest request, Long operatorId);
}
