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
@TableName("user_info")
@Schema(name = "Info", description = "")
public class Info implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "登录用户名")
    @TableField("username")
    private String username;

    @Schema(description = "用户昵称")
    @TableField("nickname")
    private String nickname;

    @Schema(description = "电子邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "手机号码")
    @TableField("phone")
    private String phone;

    @Schema(description = "头像URL")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "账户状态")
    @TableField("status")
    private String status;

    @Schema(description = "创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "最后更新时间")
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
