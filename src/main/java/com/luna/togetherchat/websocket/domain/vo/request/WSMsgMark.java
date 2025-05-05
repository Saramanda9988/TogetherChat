package com.luna.togetherchat.websocket.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSMsgMark {
    private List<WSMsgMarkItem> markList;

    @Data
    public static class WSMsgMarkItem {
        @Schema(description = "操作者")
        private Long userId;
        @Schema(description = "消息id")
        private Long syncId;

        @Schema(description = "标记对应头像")
        private Long avatarId;

        /**
         * @see MessageMarkActTypeEnum
         */
        @Schema(description = "动作类型 1确认 2取消")
        private Integer actType;
    }
}
