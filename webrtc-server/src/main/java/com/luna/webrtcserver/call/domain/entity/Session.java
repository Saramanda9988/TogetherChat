package com.luna.webrtcserver.call.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("call_session")
@Schema(name = "Session", description = "")
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "会话ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long sessionId;

    @Schema(description = "通话类型：1-语音通话，2-视频通话")
    @TableField("call_type")
    private Integer callType;

    @Schema(description = "创建者用户ID")
    @TableField("creator_id")
    private Long creatorId;

    @Schema(description = "会话主题 单独通话不需要")
    @TableField("subject")
    private String subject;

    @Schema(description = "会话状态 0等待 1进行 2结束")
    @TableField("status")
    private Integer status;

    @Schema(description = "开始时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;
}
