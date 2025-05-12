package com.luna.togetherchat.websocket.domain.vo.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 通话请求的推送类
 * Author: Luna
 * Date: 2025-05-10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSCallSignalingAction {
    @Schema(description = "发起通话的用户ID")
    private Long callerId;

    @Schema(description = "接收通话的用户ID")
    private Long receiverId;

    @Schema(description = "通话会话ID")
    private Long sessionId;

    @Schema(description = "信令类型")
    private Integer type;

    @Schema(description = "通话类型：1-语音通话，2-视频通话")
    private Integer callType;

    @Schema(description = "会话类型 1一对一通话 2群聊")
    private Integer sessionType;

    @Schema(description = "通话请求的过期时间戳")
    private Long expireTime;

    @Schema(description = "拒绝原因：1-用户拒绝，2-用户忙，3-其他原因")
    private Integer rejectReason;

    @Schema(description = "挂断原因：1-正常挂断，2-网络问题，3-其他原因")
    private Integer cancelReason;
}
