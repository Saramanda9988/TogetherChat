package com.luna.common.domain.vo.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "游标翻页请求")
public class CursorPageBaseRequest {
    @Schema(description = "游标（上次翻页的最后一条记录的标识）")
    private Double cursor;
    
    @Schema(description = "每页大小")
    private Integer pageSize = 10;

    @JsonIgnore
    public Boolean isFirstPage() {
        return cursor == null;
    }
}
