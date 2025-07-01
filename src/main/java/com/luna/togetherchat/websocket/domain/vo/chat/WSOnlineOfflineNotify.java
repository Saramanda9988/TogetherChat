package com.luna.togetherchat.websocket.domain.vo.chat;

import com.luna.togetherchat.room.domain.response.RoomMemberResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:用户上下线变动的推送类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSOnlineOfflineNotify {
    private List<RoomMemberResponse> changeList = new ArrayList<>();//新的上下线用户
    private Long onlineNum;//在线人数
}
