package com.luna.roomserver.room.service;

import com.luna.roomserver.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.roomserver.room.domain.request.RoomCreateRequest;
import com.luna.roomserver.room.domain.request.RoomPageRequest;
import com.luna.roomserver.room.domain.request.RoomUpdateRequest;
import com.luna.roomserver.room.domain.response.RoomResponse;

/**
 * <p>
 *  群组服务接口
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
public interface RoomService {

    /**
     * 创建群组
     *
     * @param request 创建群组请求
     * @param userId 创建者用户ID
     * @return 群组信息
     */
    RoomResponse createRoom(RoomCreateRequest request, Long userId);

    /**
     * 获取群组信息
     *
     * @param groupId 群组ID
     * @param userId 当前用户ID
     * @return 群组信息
     */
    RoomResponse getRoomInfo(Long groupId, Long userId);

    /**
     * 删除群组
     *
     * @param groupId 群组ID
     * @param userId 当前用户ID
     */
    void deleteRoom(Long groupId, Long userId);

    /**
     * 更新群组信息
     *
     * @param request 更新群组请求
     * @param userId 当前用户ID
     * @return 更新后的群组信息
     */
    RoomResponse updateRoom(RoomUpdateRequest request, Long userId);

    /**
     * 分页获取用户的群组列表
     *
     * @param request 分页请求
     * @param userId 用户ID
     * @return 群组分页列表
     */
    CursorPageBaseResponse<RoomResponse> getUserRooms(RoomPageRequest request, Long userId);
}
