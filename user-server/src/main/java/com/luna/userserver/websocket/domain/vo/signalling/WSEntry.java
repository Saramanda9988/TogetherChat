package com.luna.userserver.websocket.domain.vo.signalling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class WSEntry extends WSBaseSignalling{
    private Long userId; // 用户ID
    private String userName; // 用户名称
    private String userAvatar; // 用户头像
}
