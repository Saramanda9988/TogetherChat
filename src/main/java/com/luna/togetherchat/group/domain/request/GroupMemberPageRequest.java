package com.luna.togetherchat.group.domain.request;

import com.luna.togetherchat.common.domain.vo.request.CursorPageBaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 群组成员分页请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "群组成员分页请求")
public class GroupMemberPageRequest extends CursorPageBaseRequest {
    
    @NotNull(message = "群组ID不能为空")
    @Schema(description = "群组ID")
    private Long groupId;
}
