package com.luna.togetherchat.call.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
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
@TableName("live_broadcast")
@Schema(name = "Broadcast", description = "")
public class Broadcast implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "直播ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联会话ID")
    @TableField("session_id")
    private Long sessionId;

    @Schema(description = "推流密钥")
    @TableField("stream_key")
    private String streamKey;

    @Schema(description = "观众数量")
    @TableField("viewer_count")
    private Integer viewerCount;

    @Schema(description = "直播状态")
    @TableField("status")
    private String status;

    @Schema(description = "开始时间")
    @TableField("started_at")
    private LocalDateTime startedAt;

    @Schema(description = "结束时间")
    @TableField("ended_at")
    private LocalDateTime endedAt;
}
