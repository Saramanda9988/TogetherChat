package com.luna.togetherchat.call.domain.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallingCancelRequest {
    @Schema(description = "发起信令的用户ID")
    private Long callerId;

    @Schema(description = "处理信令的用户ID")
    private Long receiverId;

    @Schema(description = "会话id")
    private Long sessionId;

    @Schema(description = "通话请求的过期时间戳")
    private Long expireTime;

    @Schema(description = "会话类型 1一对一通话 2群聊")
    private Integer sessionType;

    @Schema(description = "挂断原因：1-正常挂断，2-网络问题，3-其他原因")
    private Integer cancelReason;
}
