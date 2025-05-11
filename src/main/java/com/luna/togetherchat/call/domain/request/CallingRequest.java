package com.luna.togetherchat.call.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

/**
 * 通话/视频功能的请求体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallingRequest {
    @Schema(description = "发起信令的用户ID")
    private Long callerId;

    @Schema(description = "处理信令的用户ID")
    private List<Long> receiverId;

    @Schema(description = "信令类型")
    private Integer type;

    @Schema(description = "通话请求的过期时间戳")
    private Long expireTime;

    @Schema(description = "额外信息")
    private Integer extra;
}
