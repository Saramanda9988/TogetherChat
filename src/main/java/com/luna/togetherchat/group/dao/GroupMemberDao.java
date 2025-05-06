package com.luna.togetherchat.group.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.togetherchat.group.domain.entity.GroupMember;
import com.luna.togetherchat.group.mapper.GroupMemberMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> {
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
}
