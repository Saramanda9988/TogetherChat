package com.luna.togetherchat.websocket.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 挂断通话的推送类
 * Author: Luna
 * Date: 2025-05-10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSCallCancel {
    @Schema(description = "挂断通话的用户ID")
    private Long userId;
    
    @Schema(description = "通话会话ID")
    private Long sessionId;
    
    @Schema(description = "通话时长（秒）")
    private Long duration;
    
    @Schema(description = "挂断原因：1-正常挂断，2-网络问题，3-其他原因")
    private Integer cancelReason;
    
    @Schema(description = "通话类型：1-语音通话，2-视频通话")
    private Integer callType;
}
