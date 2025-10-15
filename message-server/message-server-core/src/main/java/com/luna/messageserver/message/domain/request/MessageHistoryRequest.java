package com.luna.messageserver.message.domain.request;

import com.luna.common.domain.vo.request.PageBaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 获取会话历史消息请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "获取会话历史消息请求")
public class MessageHistoryRequest extends PageBaseRequest {

    @Schema(description = "消息游标，用于分页查询")
    private Long cursor;

    @Schema(description = "每页大小，默认20")
    private Integer pageSize = 20;
}