package com.luna.togetherchat.websocket.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 通话同意的推送类
 * Author: Luna
 * Date: 2025-05-10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSCallAgree {
    @Schema(description = "发起通话的用户ID")
    private Long callerId;
    
    @Schema(description = "同意通话的用户ID")
    private Long receiverId;
    
    @Schema(description = "通话会话ID")
    private Long sessionId;
    
    @Schema(description = "通话类型：1-语音通话，2-视频通话")
    private Integer callType;
}
