package com.luna.togetherchat.group.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.togetherchat.common.domain.vo.request.CursorPageBaseRequest;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.common.utils.CursorUtils;
import com.luna.togetherchat.group.domain.entity.Group;
import com.luna.togetherchat.group.domain.entity.GroupMember;
import com.luna.togetherchat.group.domain.request.GroupPageRequest;
import com.luna.togetherchat.group.domain.response.GroupResponse;
import com.luna.togetherchat.group.enums.RoomStatusEnum;
import com.luna.togetherchat.group.mapper.GroupMemberMapper;
import cn.hutool.core.util.StrUtil;
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

    /**
     * 获取用户的群组分页列表
     *
     * @param userId 用户ID
     * @param request 分页请求
     * @param lastGroupId 上一页最后一个群组ID
     * @return 群组分页响应
     */
    public CursorPageBaseResponse<Group> getCursorPage(Long userId, GroupPageRequest request, Long lastGroupId) {
        // 先获取用户所在的群组ID列表
        List<Long> groupIds = lambdaQuery()
                .select(GroupMember::getGroupId)
                .eq(GroupMember::getUserId, userId)
                .list()
                .stream()
                .map(GroupMember::getGroupId)
                .toList();
        
        if (groupIds.isEmpty()) {
            return CursorPageBaseResponse.empty();
        }
        
        // 使用游标分页查询群组信息，过滤掉已删除的群组
        return CursorUtils.getCursorPageByMysql(
                groupDao, 
                request,
                wrapper -> {
                    wrapper.in(Group::getId, groupIds);
                    wrapper.ne(Group::getStatus, RoomStatusEnum.DELETED.getType()); // 过滤掉已删除的群组
                    wrapper.like(StrUtil.isNotBlank(request.getKeyword()), Group::getName, request.getKeyword()); // TODO：要进行索引编排
                    wrapper.le(Objects.nonNull(lastGroupId), Group::getId, lastGroupId); // 游标条件
                },
                Group::getId
        );
    }
}