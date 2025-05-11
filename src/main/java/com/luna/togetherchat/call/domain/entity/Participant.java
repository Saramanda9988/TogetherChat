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
@TableName("call_participant")
@Schema(name = "Participant", description = "")
public class Participant implements Serializable {

    // TODO:记得修改数据库表结构
    private static final long serialVersionUID = 1L;

    @Schema(description = "参与记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long participantId;

    @Schema(description = "会话ID")
    @TableField("session_id")
    private Long sessionId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "加入时间")
    @TableField("join_time")
    private LocalDateTime joinTime;

    @Schema(description = "离开时间")
    @TableField("leave_time")
    private LocalDateTime leaveTime;
}
