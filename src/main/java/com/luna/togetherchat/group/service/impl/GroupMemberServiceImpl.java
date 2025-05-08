package com.luna.togetherchat.group.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.common.enums.CommonErrorEnum;
import com.luna.togetherchat.common.exception.BusinessException;
import com.luna.togetherchat.group.dao.GroupDao;
import com.luna.togetherchat.group.dao.GroupMemberDao;
import com.luna.togetherchat.group.domain.entity.Group;
import com.luna.togetherchat.group.domain.entity.GroupMember;
import com.luna.togetherchat.group.domain.request.GroupMemberAddRequest;
import com.luna.togetherchat.group.domain.request.GroupMemberPageRequest;
import com.luna.togetherchat.group.domain.request.GroupMemberRemoveRequest;
import com.luna.togetherchat.group.domain.request.GroupMemberUpdateRequest;
import com.luna.togetherchat.group.domain.response.GroupMemberResponse;
import com.luna.togetherchat.group.enums.GroupErrorEnum;
import com.luna.togetherchat.group.enums.MemberTypeEnum;
import com.luna.togetherchat.group.enums.RoomStatusEnum;
import com.luna.togetherchat.group.service.GroupMemberService;
import com.luna.togetherchat.user.dao.UserDao;
import com.luna.togetherchat.user.domain.entity.User;
import com.luna.togetherchat.user.enums.UserErrorEnum;
import com.luna.togetherchat.websocket.domain.enums.WSRespTypeEnum;
import com.luna.togetherchat.websocket.domain.vo.request.WSBaseResp;
import com.luna.togetherchat.websocket.domain.vo.request.WSMemberChange;
import com.luna.togetherchat.websocket.service.PushService;
import com.luna.togetherchat.websocket.service.WebSocketServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
public class GroupMemberServiceImpl implements GroupMemberService {
    private final GroupDao groupDao;
    private final GroupMemberDao groupMemberDao;
    private final UserDao userDao;
    private final PushService pushService;
    private final WebSocketServiceImpl webSocketService;

    /**
     * 添加群组成员
     *
     * @param request    添加成员请求
     * @param operatorId 操作者用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGroupMembers(GroupMemberAddRequest request, Long operatorId) {
        // 检查群组是否存在
        Group group = groupDao.getById(request.getGroupId());
        if (Objects.isNull(group) || Objects.equals(group.getStatus(), RoomStatusEnum.DELETED.getType())) {
            throw new BusinessException(GroupErrorEnum.GROUP_NOT_EXIST);
        }

        // 检查操作者权限（群主或管理员）
        GroupMember operator = groupMemberDao.getMemberByGroupIdAndUserId(request.getGroupId(), operatorId);
        if (Objects.isNull(operator) ||
            (!Objects.equals(operator.getRole(), MemberTypeEnum.OWNER.getType()) &&
             !Objects.equals(operator.getRole(), MemberTypeEnum.ADMINISTRATOR.getType()))) {
            throw new BusinessException(GroupErrorEnum.NOT_ALLOWED_OPERATION);
        }

        // 添加成员
        List<Long> userIds = request.getUserIds();
        if (CollectionUtil.isEmpty(userIds)) {
            return;
        }

        // 过滤掉已经在群里的成员
        List<Long> existingMemberIds = groupMemberDao.listUserIdByGroupId(request.getGroupId());
        List<Long> newMemberIds = userIds.stream()
                .filter(userId -> !existingMemberIds.contains(userId))
                .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(newMemberIds)) {
            return;
        }

        // 创建新成员关系
        List<GroupMember> members = new ArrayList<>();
        for (Long userId : newMemberIds) {
            GroupMember member = GroupMember.builder()
                    .groupId(request.getGroupId())
                    .userId(userId)
                    .role(MemberTypeEnum.MEMBER.getType())
                    .joinTime(LocalDateTime.now())
                    .build();
            members.add(member);
        }

        // 批量保存
        groupMemberDao.saveBatch(members);

        // 推送成员变动消息
        sendMemberChangeMessage(request.getGroupId(), newMemberIds, WSMemberChange.CHANGE_TYPE_ADD);
    }

    /**
     * 获取群组成员详情
     *
     * @param groupId    群组ID
     * @param userId     用户ID
     * @param operatorId 操作者用户ID
     * @return 成员详情
     */
    @Override
    public GroupMemberResponse getMemberDetail(Long groupId, Long userId, Long operatorId) {
        // 检查群组是否存在
        Group group = groupDao.getById(groupId);
        if (group == null || Objects.equals(group.getStatus(), RoomStatusEnum.DELETED.getType())) {
            throw new BusinessException(GroupErrorEnum.GROUP_NOT_EXIST);
        }

        // 检查操作者是否在群里
        GroupMember operator = groupMemberDao.getMemberByGroupIdAndUserId(groupId, operatorId);
        if (operator == null) {
            throw new BusinessException(GroupErrorEnum.NOT_IN_GROUP);
        }

        // 获取成员信息
        GroupMember member = groupMemberDao.getMemberByGroupIdAndUserId(groupId, userId);
        if (member == null) {
            throw new BusinessException(GroupErrorEnum.MEMBER_NOT_EXIST);
        }

        // 获取用户信息
        User userInfo = userDao.getById(userId);
        if (userInfo == null) {
            throw new BusinessException(UserErrorEnum.USER_NOT_EXIST);
        }

        // 构建响应
        return GroupMemberResponse
                .builder()
                .id(member.getMemberId())
                .groupId(member.getGroupId())
                .userId(member.getUserId())
                .nickname(userInfo.getNickname())
                .avatar(userInfo.getAvatar())
                .role(member.getRole())
                .build();
    }

    /**
     * 分页获取群组成员列表
     *
     * @param request    分页请求
     * @param operatorId 操作者用户ID
     * @return 成员分页列表
     */
    @Override
    public CursorPageBaseResponse<GroupMember> getGroupMembers(GroupMemberPageRequest request, Long operatorId) {
        // 检查操作者是否在群里
        GroupMember operator = groupMemberDao.getMemberByGroupIdAndUserId(request.getGroupId(), operatorId);
        if (operator == null) {
            throw new BusinessException(GroupErrorEnum.NOT_IN_GROUP);
        }

        CursorPageBaseResponse<GroupMember> cursorPage = groupMemberDao.getCursorPage(request.getGroupId(), request, Long.MAX_VALUE);

        // 如果不是空就转换
        return cursorPage.isEmpty()
                ? CursorPageBaseResponse.empty()
                : CursorPageBaseResponse.init(cursorPage, cursorPage.getList());
    }

    /**
     * 移除群组成员
     *
     * @param request    移除成员请求
     * @param operatorId 操作者用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeGroupMember(GroupMemberRemoveRequest request, Long operatorId) {
        // 检查群组是否存在
        Group group = groupDao.getById(request.getGroupId());
        if (group == null || Objects.equals(group.getStatus(), RoomStatusEnum.DELETED.getType())) {
            throw new BusinessException(GroupErrorEnum.GROUP_NOT_EXIST);
        }

        // 检查要移除的成员是否存在
        GroupMember targetMember = groupMemberDao.getMemberByGroupIdAndUserId(request.getGroupId(), request.getUserId());
        if (targetMember == null) {
            throw new BusinessException(GroupErrorEnum.MEMBER_NOT_EXIST);
        }

        // 检查操作者权限
        GroupMember operator = groupMemberDao.getMemberByGroupIdAndUserId(request.getGroupId(), operatorId);
        if (operator == null) {
            throw new BusinessException(GroupErrorEnum.NOT_IN_GROUP);
        }

        // 权限检查：群主可以踢任何人，管理员只能踢普通成员，普通成员只能踢自己（退群）
        boolean isOwner = Objects.equals(operator.getRole(), MemberTypeEnum.OWNER.getType());
        boolean isAdmin = Objects.equals(operator.getRole(), MemberTypeEnum.ADMINISTRATOR.getType());
        boolean isSelf = Objects.equals(operatorId, request.getUserId());

        // 群主不能被踢（除非自己退群）
        if (Objects.equals(targetMember.getRole(), MemberTypeEnum.OWNER.getType()) && !isSelf) {
            throw new BusinessException(GroupErrorEnum.NOT_ALLOWED_OPERATION);
        }

        // 管理员只能被群主踢或自己退群
        if (Objects.equals(targetMember.getRole(), MemberTypeEnum.ADMINISTRATOR.getType())
                && !isOwner && !isSelf) {
            throw new BusinessException(GroupErrorEnum.NOT_ALLOWED_OPERATION);
        }

        // 普通成员可以被群主、管理员踢或自己退群
        if (Objects.equals(targetMember.getRole(), MemberTypeEnum.MEMBER.getType())
                && !isOwner && !isAdmin && !isSelf) {
            throw new BusinessException(GroupErrorEnum.NOT_ALLOWED_OPERATION);
        }

        // 移除成员
        groupMemberDao.removeMember(request.getGroupId(), request.getUserId());

        // 推送成员移除消息
        sendMemberChangeMessage(request.getGroupId(), List.of(request.getUserId()), WSMemberChange.CHANGE_TYPE_REMOVE);
    }

    /**
     * 更新群组成员角色
     *
     * @param request    更新成员角色请求
     * @param operatorId 操作者用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberRole(GroupMemberUpdateRequest request, Long operatorId) {
        // 检查群组是否存在
        Group group = groupDao.getById(request.getGroupId());
        if (group == null || Objects.equals(group.getStatus(), RoomStatusEnum.DELETED.getType())) {
            throw new BusinessException(GroupErrorEnum.GROUP_NOT_EXIST);
        }

        // 检查要更新的成员是否存在
        GroupMember targetMember = groupMemberDao.getMemberByGroupIdAndUserId(request.getGroupId(), request.getUserId());
        if (targetMember == null) {
            throw new BusinessException(GroupErrorEnum.MEMBER_NOT_EXIST);
        }

        // 检查操作者权限
        GroupMember operator = groupMemberDao.getMemberByGroupIdAndUserId(request.getGroupId(), operatorId);
        if (operator == null) {
            throw new BusinessException(GroupErrorEnum.NOT_IN_GROUP);
        }

        // 只有群主可以更改成员角色
        if (!Objects.equals(operator.getRole(), MemberTypeEnum.OWNER.getType())) {
            throw new BusinessException(GroupErrorEnum.NOT_ALLOWED_OPERATION);
        }

        // 不能将其他成员设置为群主
        if (Objects.equals(request.getRole(), MemberTypeEnum.OWNER.getType())) {
            throw new BusinessException(GroupErrorEnum.NOT_ALLOWED_OPERATION);
        }

        // 更新成员角色
        targetMember.setRole(request.getRole());
        groupMemberDao.updateById(targetMember);

        // 推送成员角色变更消息
        sendMemberChangeMessage(request.getGroupId(), List.of(request.getUserId()), WSMemberChange.CHANGE_TYPE_UPDATE);
    }

    /**
     * 发送成员变动消息
     *
     * @param groupId    群组ID
     * @param userIds    用户ID列表
     * @param changeType 变动类型
     */
    private void sendMemberChangeMessage(Long groupId, List<Long> userIds, Integer changeType) {
        // 构建成员变动消息
        WSMemberChange memberChange = WSMemberChange.builder()
                .roomId(groupId)
                .userIds(userIds)
                .changeType(changeType)
                .lastOptTime(new Date())
                .build();

        // 构建 WebSocket 响应
        WSBaseResp<WSMemberChange> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MEMBER_CHANGE.getType());
        wsBaseResp.setData(memberChange);

        // 获取群组所有成员
        List<Long> allMemberIds = groupMemberDao.listUserIdByGroupId(groupId);

        // 推送消息给群组所有成员
        pushService.sendPushMsg(wsBaseResp, allMemberIds);
    }
}
