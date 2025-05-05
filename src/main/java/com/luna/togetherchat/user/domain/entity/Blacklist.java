package com.luna.togetherchat.user.domain.entity;


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
@TableName("user_blacklist")
@Schema(name = "Blacklist", description = "")
public class Blacklist implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "操作拉黑的用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "被拉黑的用户ID")
    @TableField("blocked_user_id")
    private Long blockedUserId;

    @Schema(description = "拉黑原因")
    @TableField("reason")
    private String reason;

    @Schema(description = "拉黑时间")
    @TableField("created_at")
    private LocalDateTime createdAt;
}
