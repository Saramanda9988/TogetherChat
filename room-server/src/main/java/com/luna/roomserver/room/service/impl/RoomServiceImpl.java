package com.luna.roomserver.room.service.impl;

import com.luna.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.roomserver.room.dao.RoomDao;
import com.luna.roomserver.room.dao.RoomMemberDao;
import com.luna.roomserver.room.domain.request.RoomCreateRequest;
import com.luna.roomserver.room.domain.request.RoomPageRequest;
import com.luna.roomserver.room.domain.request.RoomUpdateRequest;
import com.luna.roomserver.room.domain.response.RoomResponse;
import com.luna.roomserver.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

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
@DubboService
public class RoomServiceImpl implements RoomService {

    private final RoomDao roomDao;
    private final RoomMemberDao roomMemberDao;

    /**
     * 创建群组
     *
     * @param request 创建群组请求
     * @param userId  创建者用户ID
     * @return 群组信息
     */
    @Override
    public RoomResponse createRoom(RoomCreateRequest request, Long userId) {
        return null;
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
        return null;
    }

    /**
     * 删除群组
     *
     * @param groupId 群组ID
     * @param userId  当前用户ID
     */
    @Override
    public void deleteRoom(Long groupId, Long userId) {

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
        return null;
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
        return null;
    }
}
