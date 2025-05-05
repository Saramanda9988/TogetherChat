package com.luna.togetherchat.websocket.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSBackgroundChange {
    @Schema(description = "房间ID")
    private Long roomId;
    @Schema(description = "背景图片URL")
    private String backgroundUrl;
}
