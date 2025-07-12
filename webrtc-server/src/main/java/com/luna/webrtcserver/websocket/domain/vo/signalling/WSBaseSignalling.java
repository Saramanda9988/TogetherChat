package com.luna.webrtcserver.websocket.domain.vo.signalling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WSBaseSignalling {
    Integer type; // 信令类型

    Long sourceId; // 信令来源用户ID

    Long targetId; // 信令目标用户ID

    Long meetId; // 信令所属会议ID

    String key; // 信令的唯一标识符
}
