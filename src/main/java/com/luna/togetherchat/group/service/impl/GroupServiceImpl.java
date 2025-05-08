package com.luna.togetherchat.group.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.common.enums.CommonErrorEnum;
import com.luna.togetherchat.common.exception.BusinessException;
import com.luna.togetherchat.common.utils.CursorUtils;
import com.luna.togetherchat.group.dao.GroupDao;
import com.luna.togetherchat.group.dao.GroupMemberDao;
import com.luna.togetherchat.group.domain.entity.Group;
import com.luna.togetherchat.group.domain.entity.GroupMember;
import com.luna.togetherchat.group.domain.request.GroupCreateRequest;
import com.luna.togetherchat.group.domain.request.GroupPageRequest;
import com.luna.togetherchat.group.domain.request.GroupUpdateRequest;
import com.luna.togetherchat.group.domain.response.GroupResponse;
import com.luna.togetherchat.group.enums.GroupErrorEnum;
import com.luna.togetherchat.group.enums.MemberTypeEnum;
import com.luna.togetherchat.group.enums.RoomStatusEnum;
import com.luna.togetherchat.group.service.GroupService;
import com.luna.togetherchat.websocket.domain.enums.WSRespTypeEnum;
import com.luna.togetherchat.websocket.domain.vo.request.WSBaseResp;
import com.luna.togetherchat.websocket.domain.vo.request.WSMemberChange;
import com.luna.togetherchat.websocket.domain.vo.request.WSRoomDissolve;
import com.luna.togetherchat.websocket.service.PushService;
import com.luna.togetherchat.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
public class GroupServiceImpl implements GroupService {

    private final GroupDao groupDao;
    private final GroupMemberDao groupMemberDao;
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
    public GroupResponse createGroup(GroupCreateRequest request, Long userId) {
        // 创建群组
        Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .avatar(request.getAvatar())
                .creatorId(userId)
                .type(request.getType())
                .status(RoomStatusEnum.ACTIVE.getType())
                .build();
        groupDao.save(group);

        List<Long> memberIds = request.getMemberIds();
        // 先判断是群聊还是单聊
        if (Objects.equals(request.getType(), RoomStatusEnum.GROUP.getType())) {
            // 添加其他成员
            if (CollectionUtil.isNotEmpty(memberIds)) {
                List<GroupMember> members = new ArrayList<>();
                memberIds.forEach(memberId -> {
                    GroupMember member = GroupMember.builder()
                            .groupId(group.getId())
                            .userId(memberId)
                            .join_time(LocalDateTime.now())
                            .build();
                    if (Objects.equals(memberId, userId)) {
                        member.setRole(MemberTypeEnum.OWNER.getType());
                    } else {
                        member.setRole(MemberTypeEnum.MEMBER.getType());
                    }
                    members.add(member);
                });
                if (!members.isEmpty()) {
                    groupMemberDao.saveBatch(members);
                }
            }
        } else {
            // 单聊就两个人，不会出现错误
            if (memberIds.size() != 2) {
                throw new BusinessException(CommonErrorEnum.PARAMS_ERROR);
            }
            List<GroupMember> members = new ArrayList<>();
            memberIds.forEach(memberId -> {
                GroupMember member = GroupMember.builder()
                        .groupId(group.getId())
                        .userId(memberId)
                        .role(MemberTypeEnum.ONE.getType())
                        .join_time(LocalDateTime.now())
                        .build();
                members.add(member);
            });
            if (!members.isEmpty()) {
                groupMemberDao.saveBatch(members);
            }
        }

        // 返回群组信息
        GroupResponse response = BeanUtil.copyProperties(group, GroupResponse.class);
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
    public GroupResponse getGroupInfo(Long groupId, Long userId) {
        // 检查群组是否存在
        Group group = groupDao.getById(groupId);
        if (group == null || Objects.equals(group.getStatus(), RoomStatusEnum.DELETED.getType())) {
            throw new BusinessException(GroupErrorEnum.GROUP_NOT_EXIST);
        }

        // 检查用户是否在群组中
        GroupMember member = groupMemberDao.getMemberByGroupIdAndUserId(groupId, userId);
        if (member == null) {
            throw new BusinessException(GroupErrorEnum.USER_NOT_IN_GROUP);
        }

        // 获取群组成员数量
        List<Long> memberIds = groupMemberDao.listUserIdByGroupId(groupId);
        int memberCount = memberIds.size();

        // 返回群组信息
        GroupResponse response = BeanUtil.copyProperties(group, GroupResponse.class);
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
    public void deleteGroup(Long groupId, Long userId) {
        // 检查群组是否存在
        Group group = groupDao.getById(groupId);
        if (group == null || Objects.equals(group.getStatus(), RoomStatusEnum.DELETED.getType())) {
            throw new BusinessException(GroupErrorEnum.GROUP_NOT_EXIST);
        }

        // 检查是否是群主
        GroupMember member = groupMemberDao.getMemberByGroupIdAndUserId(groupId, userId);
        if (member == null || !Objects.equals(member.getRole(), MemberTypeEnum.OWNER.getType())) {
            throw new BusinessException(GroupErrorEnum.NOT_ALLOWED_OPERATION);
        }

        // 逻辑删除群
        group.setStatus(RoomStatusEnum.DELETED.getType());
        groupDao.updateById(group);

        // 推送解散消息
        List<Long> receiverIds = groupMemberDao.listUserIdByGroupId(groupId);
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
    public GroupResponse updateGroup(GroupUpdateRequest request, Long userId) {
        // 检查群组是否存在
        Group group = groupDao.getById(request.getId());
        if (group == null) {
            throw new BusinessException(GroupErrorEnum.GROUP_NOT_EXIST);
        }

        // 检查权限（群主或管理员）
        GroupMember member = groupMemberDao.getMemberByGroupIdAndUserId(request.getId(), userId);
        if (member == null ||
            (!Objects.equals(member.getRole(), MemberTypeEnum.OWNER.getType()) &&
                    !Objects.equals(member.getRole(), MemberTypeEnum.ADMINISTRATOR.getType()))) {
            throw new BusinessException(GroupErrorEnum.NOT_ALLOWED_OPERATION);
        }
        group.setName(request.getName());
        group.setAvatar(request.getAvatar());
        group.setDescription(request.getDescription());

        groupDao.updateById(group);

        // 获取群组成员数量
        List<Long> memberIds = groupMemberDao.listUserIdByGroupId(request.getId());
        int memberCount = memberIds.size();

        // 返回群组信息
        GroupResponse response = GroupResponse
                .builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .avatar(group.getAvatar())
                .createdAt(group.getCreateTime())
                .updatedAt(group.getUpdateTime())
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
    public CursorPageBaseResponse<GroupResponse> getUserGroups(GroupPageRequest request, Long userId) {
        CursorPageBaseResponse<Group> cursorPage = groupMemberDao.getCursorPage(userId, request, Long.MAX_VALUE);

        List<GroupResponse> list = cursorPage.getList().stream().map(group -> GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .avatar(group.getAvatar())
                .createdAt(group.getCreateTime())
                .updatedAt(group.getUpdateTime())
                .build()).toList();

        // 如果不是空就转换
        return cursorPage.isEmpty()
                ? CursorPageBaseResponse.empty()
                : CursorPageBaseResponse.init(cursorPage, list);
    }
}
