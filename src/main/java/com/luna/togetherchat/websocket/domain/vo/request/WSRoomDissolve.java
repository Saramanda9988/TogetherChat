package com.luna.togetherchat.websocket.domain.vo.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 群组解散通知
 * Author: JinYu
 * Date: 2025-04-07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSRoomDissolve {
    @Schema(description = "解散的群组ID")
    private Long roomId;
}
