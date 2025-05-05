package com.luna.togetherchat.chat.domain.entity;


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
@TableName("chat_message")
@Schema(name = "Message", description = "")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "所属群组ID")
    @TableField("group_id")
    private Long groupId;

    @Schema(description = "发送者用户ID")
    @TableField("sender_id")
    private Long senderId;

    @Schema(description = "消息内容")
    @TableField("content")
    private String content;

    @Schema(description = "消息类型")
    @TableField("type")
    private String type;

    @Schema(description = "消息状态")
    @TableField("status")
    private String status;

    @Schema(description = "发送时间")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "最后更新时间")
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
