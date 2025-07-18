package com.luna.roomserver.room.service;

import com.luna.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.roomserver.room.domain.entity.RoomMember;
import com.luna.roomserver.room.domain.request.RoomMemberAddRequest;
import com.luna.roomserver.room.domain.request.RoomMemberPageRequest;
import com.luna.roomserver.room.domain.request.RoomMemberRemoveRequest;
import com.luna.roomserver.room.domain.request.RoomMemberUpdateRequest;
import com.luna.roomserver.room.domain.response.RoomMemberResponse;
import jakarta.validation.Valid;

import java.util.List;

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

    /**
     * 分页获取群组用户信息
     * @param request
     * @param userId
     * @return
     */
    CursorPageBaseResponse<RoomMember> getRoomMembers(@Valid RoomMemberPageRequest request, Long userId);

    boolean validMember(Long groupId, Long ownId, Long userId);

    List<Long> listUserIdByGroupId(Long groupId);
}
