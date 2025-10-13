package com.luna.conversationserver.conversation.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * <p>
 * 
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-10-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("conversation_member")
@Schema(name = "ConversationMember", description = "")
public class ConversationMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "成员关系ID")
    @TableId(value = "member_id", type = IdType.AUTO)
    private Long memberId;

    @Schema(description = "群组ID")
    @TableField("conversation_id")
    private Long conversationId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "成员角色")
    @TableField("role")
    private Integer role;

    @Schema(description = "加入时间")
    @TableField("join_time")
    private LocalDateTime joinTime;

    @Schema(description = "角色昵称")
    @TableField("nickname")
    private String nickname;

    @Schema(description = "状态 1正常 2禁言 3退出")
    @TableField("state")
    private Byte state;
}
