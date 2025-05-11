package com.luna.togetherchat.websocket.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 通话拒绝的推送类
 * Author: Luna
 * Date: 2025-05-10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSCallReject {
    @Schema(description = "发起通话的用户ID")
    private Long callerId;
    
    @Schema(description = "拒绝通话的用户ID")
    private Long receiverId;
    
    @Schema(description = "拒绝原因：1-用户拒绝，2-用户忙，3-其他原因")
    private Integer rejectReason;
    
    @Schema(description = "通话类型：1-语音通话，2-视频通话")
    private Integer callType;
}
