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
@TableName("call_session")
@Schema(name = "Session", description = "")
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "会话ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "会话类型")
    @TableField("type")
    private String type;

    @Schema(description = "创建者用户ID")
    @TableField("creator_id")
    private Long creatorId;

    @Schema(description = "会话主题")
    @TableField("subject")
    private String subject;

    @Schema(description = "会话状态")
    @TableField("status")
    private String status;

    @Schema(description = "开始时间")
    @TableField("started_at")
    private LocalDateTime startedAt;

    @Schema(description = "结束时间")
    @TableField("ended_at")
    private LocalDateTime endedAt;
}
