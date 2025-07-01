package com.luna.togetherchat.room.dao;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.common.utils.CursorUtils;
import com.luna.togetherchat.room.domain.entity.Room;
import com.luna.togetherchat.room.domain.request.RoomPageRequest;
import com.luna.togetherchat.room.enums.RoomStatusEnum;
import com.luna.togetherchat.room.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RoomDao extends ServiceImpl<RoomMapper, Room>{
    private final RoomMemberDao roomMemberDao;

    /**
     * 获取用户的群组分页列表
     *
     * @param userId 用户ID
     * @param request 分页请求
     * @param lastGroupId 上一页最后一个群组ID
     * @return 群组分页响应
     */
    public CursorPageBaseResponse<Room> getCursorPage(Long userId, RoomPageRequest request, Long lastGroupId) {
        // 先获取用户所在的群组ID列表
        List<Long> groupIds = roomMemberDao.getRoomIdByUserId(userId);

        if (groupIds.isEmpty()) {
            return CursorPageBaseResponse.empty();
        }

        // 使用游标分页查询群组信息，过滤掉已删除的群组
        return CursorUtils.getCursorPageByMysql(
                this,
                request,
                wrapper -> {
                    wrapper.in(Room::getRoomId, groupIds);
                    wrapper.ne(Room::getStatus, RoomStatusEnum.DELETED.getType()); // 过滤掉已删除的群组
                    wrapper.like(StrUtil.isNotBlank(request.getKeyword()), Room::getName, request.getKeyword());
                    wrapper.le(Objects.nonNull(lastGroupId), Room::getRoomId, lastGroupId); // 游标条件
                },
                Room::getRoomId
        );
    }
}
