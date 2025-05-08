package com.luna.togetherchat.group.dao;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.togetherchat.common.domain.vo.request.CursorPageBaseRequest;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.common.utils.CursorUtils;
import com.luna.togetherchat.group.domain.entity.Group;
import com.luna.togetherchat.group.domain.entity.GroupMember;
import com.luna.togetherchat.group.domain.request.GroupPageRequest;
import com.luna.togetherchat.group.domain.response.GroupResponse;
import com.luna.togetherchat.group.enums.RoomStatusEnum;
import com.luna.togetherchat.group.mapper.GroupMapper;
import com.luna.togetherchat.group.mapper.GroupMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupDao extends ServiceImpl<GroupMapper, Group>{
    private final GroupMemberDao groupMemberDao;

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
        List<Long> groupIds = groupMemberDao.getGroupIdByUserId(userId);

        if (groupIds.isEmpty()) {
            return CursorPageBaseResponse.empty();
        }

        // 使用游标分页查询群组信息，过滤掉已删除的群组
        return CursorUtils.getCursorPageByMysql(
                this,
                request,
                wrapper -> {
                    wrapper.in(Group::getGroupId, groupIds);
                    wrapper.ne(Group::getStatus, RoomStatusEnum.DELETED.getType()); // 过滤掉已删除的群组
                    wrapper.like(StrUtil.isNotBlank(request.getKeyword()), Group::getName, request.getKeyword());
                    wrapper.le(Objects.nonNull(lastGroupId), Group::getGroupId, lastGroupId); // 游标条件
                },
                Group::getGroupId
        );
    }
}
