package com.luna.togetherchat.group.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.common.utils.CursorUtils;
import com.luna.togetherchat.group.domain.entity.GroupMember;
import com.luna.togetherchat.group.domain.request.GroupMemberPageRequest;
import com.luna.togetherchat.group.mapper.GroupMemberMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> {
    private final GroupDao groupDao;
    
    /**
     * 通过房间ID获取房间中所有成员用户ID
     *
     * @param groupId 房间ID
     * @return 群成员用户ID列表
     */
    public List<Long> listUserIdByGroupId(Long groupId) {
        return lambdaQuery()
                .select(GroupMember::getGroupId, GroupMember::getUserId)
                .eq(GroupMember::getGroupId, groupId)
                .list()
                .stream()
                .map(GroupMember::getUserId)
                .toList();
    }

    public GroupMember getMemberByGroupIdAndUserId(@NotNull Long groupId, Long userId) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUserId, userId)
                .one();
    }

    public List<Long> getGroupByUserId(Long userId) {
        return lambdaQuery()
                .eq(GroupMember::getUserId, userId)
                .list()
                .stream()
                .map(GroupMember::getGroupId)
                .collect(Collectors.toList());
    }

    public List<Long> getGroupIdByUserId(Long userId) {
        return lambdaQuery()
                .select(GroupMember::getGroupId)
                .eq(GroupMember::getUserId, userId)
                .list()
                .stream()
                .map(GroupMember::getGroupId)
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
    public CursorPageBaseResponse<GroupMember> getCursorPage(Long groupId, GroupMemberPageRequest request, Long lastMemberId) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
                    wrapper.eq(GroupMember::getGroupId, groupId);
                    wrapper.le(Objects.nonNull(lastMemberId), GroupMember::getMemberId, lastMemberId);
                },
                GroupMember::getMemberId
        );
    }

    public void removeMember(Long groupId, Long userId) {
        lambdaUpdate()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUserId, userId)
                .remove();
    }
}