package com.luna.roomserver.room.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.luna.roomserver.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.roomserver.common.enums.CommonErrorEnum;
import com.luna.roomserver.common.exception.BusinessException;
import com.luna.roomserver.room.dao.RoomDao;
import com.luna.roomserver.room.dao.RoomMemberDao;
import com.luna.roomserver.room.domain.entity.Room;
import com.luna.roomserver.room.domain.entity.RoomMember;
import com.luna.roomserver.room.domain.request.RoomCreateRequest;
import com.luna.roomserver.room.domain.request.RoomPageRequest;
import com.luna.roomserver.room.domain.request.RoomUpdateRequest;
import com.luna.roomserver.room.domain.response.RoomResponse;
import com.luna.roomserver.room.enums.RoomErrorEnum;
import com.luna.roomserver.room.enums.MemberTypeEnum;
import com.luna.roomserver.room.enums.RoomStatusEnum;
import com.luna.roomserver.room.service.RoomService;
import com.luna.roomserver.websocket.domain.enums.WSRespTypeEnum;
import com.luna.roomserver.websocket.domain.vo.WSBaseResp;
import com.luna.roomserver.websocket.domain.vo.chat.WSRoomDissolve;
import com.luna.roomserver.websocket.service.PushService;
import com.luna.roomserver.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  群组服务实现类
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomDao roomDao;
    private final RoomMemberDao roomMemberDao;
    private final WebSocketService webSocketService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PushService pushService;

    /**
     * 创建群组
     *
     * @param request 创建群组请求
     * @param userId  创建者用户ID
     * @return 群组信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomResponse createRoom(RoomCreateRequest request, Long userId) {
        // 创建群组
        Room room = Room.builder()
                .name(request.getName())
                .description(request.getDescription())
                .avatar(request.getAvatar())
                .creatorId(userId)
                .type(request.getType())
                .status(RoomStatusEnum.ACTIVE.getType())
                .build();
        roomDao.save(room);

        List<Long> memberIds = request.getMemberIds();
        // 先判断是群聊还是单聊
        if (Objects.equals(request.getType(), RoomStatusEnum.GROUP.getType())) {
            // 添加其他成员
            if (CollectionUtil.isNotEmpty(memberIds)) {
                List<RoomMember> members = new ArrayList<>();
                memberIds.forEach(memberId -> {
                    RoomMember member = RoomMember.builder()
                            .roomId(room.getRoomId())
                            .userId(memberId)
                            .joinTime(LocalDateTime.now())
                            .build();
                    if (Objects.equals(memberId, userId)) {
                        member.setRole(MemberTypeEnum.OWNER.getType());
                    } else {
                        member.setRole(MemberTypeEnum.MEMBER.getType());
                    }
                    members.add(member);
                });
                if (!members.isEmpty()) {
                    roomMemberDao.saveBatch(members);
                }
            }
        } else {
            // 单聊就两个人，不会出现错误
            if (memberIds.size() != 2) {
                throw new BusinessException(CommonErrorEnum.PARAMS_ERROR);
            }
            List<RoomMember> members = new ArrayList<>();
            memberIds.forEach(memberId -> {
                RoomMember member = RoomMember.builder()
                        .roomId(room.getRoomId())
                        .userId(memberId)
                        .role(MemberTypeEnum.ONE.getType())
                        .joinTime(LocalDateTime.now())
                        .build();
                members.add(member);
            });
            if (!members.isEmpty()) {
                roomMemberDao.saveBatch(members);
            }
        }

        // 返回群组信息
        RoomResponse response = BeanUtil.copyProperties(room, RoomResponse.class);
        response.setMemberCount(CollectionUtil.isEmpty(memberIds) ? 1 : memberIds.size() + 1);
        return response;
    }

    /**
     * 获取群组信息
     *
     * @param groupId 群组ID
     * @param userId  当前用户ID
     * @return 群组信息
     */
    @Override
    public RoomResponse getRoomInfo(Long groupId, Long userId) {
        // 检查群组是否存在
        Room room = roomDao.getById(groupId);
        if (room == null || Objects.equals(room.getStatus(), RoomStatusEnum.DELETED.getType())) {
            throw new BusinessException(RoomErrorEnum.GROUP_NOT_EXIST);
        }

        // 检查用户是否在群组中
        RoomMember member = roomMemberDao.getMemberByGroupIdAndUserId(groupId, userId);
        if (member == null) {
            throw new BusinessException(RoomErrorEnum.USER_NOT_IN_GROUP);
        }

        // 获取群组成员数量
        List<Long> memberIds = roomMemberDao.listUserIdByGroupId(groupId);
        int memberCount = memberIds.size();

        // 返回群组信息
        RoomResponse response = BeanUtil.copyProperties(room, RoomResponse.class);
        response.setMemberCount(memberCount);
        return response;
    }

    /**
     * 删除群组
     *
     *
     * @param groupId 群组ID
     * @param userId  当前用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoom(Long groupId, Long userId) {
        // 检查群组是否存在
        Room room = roomDao.getById(groupId);
        if (room == null || Objects.equals(room.getStatus(), RoomStatusEnum.DELETED.getType())) {
            throw new BusinessException(RoomErrorEnum.GROUP_NOT_EXIST);
        }

        // 检查是否是群主
        RoomMember member = roomMemberDao.getMemberByGroupIdAndUserId(groupId, userId);
        if (member == null || !Objects.equals(member.getRole(), MemberTypeEnum.OWNER.getType())) {
            throw new BusinessException(RoomErrorEnum.NOT_ALLOWED_OPERATION);
        }

        // 逻辑删除群
        room.setStatus(RoomStatusEnum.DELETED.getType());
        roomDao.updateById(room);

        // 推送解散消息
        List<Long> receiverIds = roomMemberDao.listUserIdByGroupId(groupId);
        WSRoomDissolve roomDissolve = new WSRoomDissolve(groupId);
        WSBaseResp<WSRoomDissolve> response = new WSBaseResp<>(WSRespTypeEnum.ROOM_DISSOLVE.getType(), roomDissolve);
        pushService.sendPushMsg(response, receiverIds);
    }

    /**
     * 更新群组信息
     *
     * @param request 更新群组请求
     * @param userId  当前用户ID
     * @return 更新后的群组信息
     */
    @Override
    public RoomResponse updateRoom(RoomUpdateRequest request, Long userId) {
        // 检查群组是否存在
        Room room = roomDao.getById(request.getId());
        if (room == null) {
            throw new BusinessException(RoomErrorEnum.GROUP_NOT_EXIST);
        }

        // 检查权限（群主或管理员）
        RoomMember member = roomMemberDao.getMemberByGroupIdAndUserId(request.getId(), userId);
        if (member == null ||
            (!Objects.equals(member.getRole(), MemberTypeEnum.OWNER.getType()) &&
                    !Objects.equals(member.getRole(), MemberTypeEnum.ADMINISTRATOR.getType()))) {
            throw new BusinessException(RoomErrorEnum.NOT_ALLOWED_OPERATION);
        }
        room.setName(request.getName());
        room.setAvatar(request.getAvatar());
        room.setDescription(request.getDescription());

        roomDao.updateById(room);

        // 获取群组成员数量
        List<Long> memberIds = roomMemberDao.listUserIdByGroupId(request.getId());
        int memberCount = memberIds.size();

        // 返回群组信息
        RoomResponse response = RoomResponse
                .builder()
                .id(room.getRoomId())
                .name(room.getName())
                .description(room.getDescription())
                .avatar(room.getAvatar())
                .createdAt(room.getCreateTime())
                .updatedAt(room.getUpdateTime())
                .build();
        response.setMemberCount(memberCount);
        return response;
    }

    /**
     * 分页获取用户的群组列表
     *
     * @param request 分页请求
     * @param userId  用户ID
     * @return 群组分页列表
     */
    @Override
    public CursorPageBaseResponse<RoomResponse> getUserRooms(RoomPageRequest request, Long userId) {
        CursorPageBaseResponse<Room> cursorPage = roomDao.getCursorPage(userId, request, Long.MAX_VALUE);

        List<RoomResponse> list = cursorPage.getList().stream().map(room -> RoomResponse.builder()
                .id(room.getRoomId())
                .name(room.getName())
                .description(room.getDescription())
                .avatar(room.getAvatar())
                .createdAt(room.getCreateTime())
                .updatedAt(room.getUpdateTime())
                .build()).toList();

        // 如果不是空就转换
        return cursorPage.isEmpty()
                ? CursorPageBaseResponse.empty()
                : CursorPageBaseResponse.init(cursorPage, list);
    }
}
