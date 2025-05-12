package com.luna.togetherchat.call.domain.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallingAcceptRequest {
    @Schema(description = "发起信令的用户ID")
    private Long callerId;

    @Schema(description = "接受信令的用户ID")
    private Long receiverId;

    @Schema(description = "会话id")
    private Long sessionId;

    @Schema(description = "会话类型 1一对一通话 2群聊")
    private Integer sessionType;

    @Schema(description = "信令类型")
    private Integer type;
}
