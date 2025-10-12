package com.luna.roomserver.room.service.impl;

import com.luna.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.roomserver.room.dao.RoomDao;
import com.luna.roomserver.room.domain.entity.RoomMember;
import com.luna.roomserver.room.domain.request.RoomMemberAddRequest;
import com.luna.roomserver.room.domain.request.RoomMemberPageRequest;
import com.luna.roomserver.room.domain.request.RoomMemberRemoveRequest;
import com.luna.roomserver.room.domain.request.RoomMemberUpdateRequest;
import com.luna.roomserver.room.service.RoomMemberService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  群组成员服务实现类
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
@Service
@RequiredArgsConstructor
@DubboService
public class RoomMemberServiceImpl implements RoomMemberService {

    private final RoomDao roomDao;

    /**
     * 添加群组成员
     *
     * @param request    添加成员请求
     * @param operatorId 操作者用户ID
     */
    @Override
    public void addRoomMembers(RoomMemberAddRequest request, Long operatorId) {

    }

    /**
     * 移除群组成员
     *
     * @param request    移除成员请求
     * @param operatorId 操作者用户ID
     */
    @Override
    public void removeRoomMember(RoomMemberRemoveRequest request, Long operatorId) {

    }

    /**
     * 更新群组成员角色
     *
     * @param request    更新成员角色请求
     * @param operatorId 操作者用户ID
     */
    @Override
    public void updateMemberRole(RoomMemberUpdateRequest request, Long operatorId) {

    }

    /**
     * 分页获取群组用户信息
     *
     * @param request
     * @param userId
     * @return
     */
    @Override
    public CursorPageBaseResponse<RoomMember> getRoomMembers(RoomMemberPageRequest request, Long userId) {
        return null;
    }

    @Override
    public boolean validMember(Long groupId, Long ownId, Long userId) {
        return false;
    }

    @Override
    public List<Long> listUserIdByGroupId(Long groupId) {
        return List.of();
    }

}
