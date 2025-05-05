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
@TableName("collab_document")
@Schema(name = "Document", description = "")
public class Document implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "文档ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "文档名称")
    @TableField("name")
    private String name;

    @Schema(description = "创建者用户ID")
    @TableField("creator_id")
    private Long creatorId;

    @Schema(description = "文档内容")
    @TableField("content")
    private String content;

    @Schema(description = "版本号")
    @TableField("version")
    private Integer version;

    @Schema(description = "创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "最后更新时间")
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
