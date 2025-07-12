package com.luna.roomserver.room.domain.request;

import com.luna.roomserver.common.domain.vo.request.CursorPageBaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 群组分页请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "群组分页请求")
public class RoomPageRequest extends CursorPageBaseRequest {
    
    @Schema(description = "群组名称关键字")
    private String keyword;
}
