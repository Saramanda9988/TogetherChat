package com.luna.webrtcserver.call.domain.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.luna.webrtcserver.call.domain.entity.Participant;
import com.luna.webrtcserver.call.domain.entity.Session;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponse {
    @Schema(description = "会话ID")
    private Long sessionId;

    @Schema(description = "通话类型：1-语音通话，2-视频通话")
    private Integer callType;

    @Schema(description = "创建者用户ID")
    private Long creatorId;

    @Schema(description = "会话主题 单独通话不需要")
    private String subject;

    @Schema(description = "会话状态 0等待 1进行 2结束")
    private Integer status;

    @Schema(description = "开始时间")
    private LocalDateTime createTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
