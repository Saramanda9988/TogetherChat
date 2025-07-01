package com.luna.togetherchat.websocket.domain.vo.signalling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WSEntry extends WSBaseSignalling{
    private Long userId; // 用户ID
    private String userName; // 用户名称
    private String userAvatar; // 用户头像
}
