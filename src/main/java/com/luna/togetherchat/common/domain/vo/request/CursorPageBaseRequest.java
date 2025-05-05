package com.luna.togetherchat.common.domain.vo.request;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    public Page plusPage() {
        return new Page(1, this.pageSize, false);
    }

    @JsonIgnore
    public Boolean isFirstPage() {
        return cursor == null;
    }
}
