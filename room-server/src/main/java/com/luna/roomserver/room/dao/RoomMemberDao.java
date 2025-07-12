package com.luna.roomserver.room.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.roomserver.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.roomserver.common.utils.CursorUtils;
import com.luna.roomserver.room.domain.entity.RoomMember;
import com.luna.roomserver.room.domain.request.RoomMemberPageRequest;
import com.luna.roomserver.room.mapper.RoomMemberMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RoomMemberDao extends ServiceImpl<RoomMemberMapper, RoomMember> {
    
    /**
     * 通过房间ID获取房间中所有成员用户ID
     *
     * @param groupId 房间ID
     * @return 群成员用户ID列表
     */
    public List<Long> listUserIdByGroupId(Long groupId) {
        return lambdaQuery()
                .select(RoomMember::getRoomId, RoomMember::getUserId)
                .eq(RoomMember::getRoomId, groupId)
                .list()
                .stream()
                .map(RoomMember::getUserId)
                .toList();
    }

    public RoomMember getMemberByGroupIdAndUserId(@NotNull Long groupId, Long userId) {
        return lambdaQuery()
                .eq(RoomMember::getRoomId, groupId)
                .eq(RoomMember::getUserId, userId)
                .one();
    }

    public List<Long> getRoomIdByUserId(Long userId) {
        return lambdaQuery()
                .select(RoomMember::getRoomId)
                .eq(RoomMember::getUserId, userId)
                .list()
                .stream()
                .map(RoomMember::getRoomId)
                .toList();
    }

    /**
     * 获取群组成员分页列表
     *
     * @param groupId 群组ID
     * @param request 分页请求
     * @param lastMemberId 上一页最后一个成员ID
     * @return 成员分页响应
     */
    public CursorPageBaseResponse<RoomMember> getCursorPage(Long groupId, RoomMemberPageRequest request, Long lastMemberId) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
                    wrapper.eq(RoomMember::getRoomId, groupId);
                    wrapper.le(Objects.nonNull(lastMemberId), RoomMember::getMemberId, lastMemberId);
                },
                RoomMember::getMemberId
        );
    }

    public void removeMember(Long groupId, Long userId) {
        lambdaUpdate()
                .eq(RoomMember::getRoomId, groupId)
                .eq(RoomMember::getUserId, userId)
                .remove();
    }
}