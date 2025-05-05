package com.luna.togetherchat.collab.domain.entity;


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
@TableName("collab_draw_operation")
@Schema(name = "DrawOperation", description = "")
public class DrawOperation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "操作ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "白板ID")
    @TableField("whiteboard_id")
    private Long whiteboardId;

    @Schema(description = "操作用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "操作类型")
    @TableField("operation_type")
    private String operationType;

    @Schema(description = "操作数据（坐标、颜色等）")
    @TableField("data")
    private String data;

    @Schema(description = "操作时间")
    @TableField("created_at")
    private LocalDateTime createdAt;
}
