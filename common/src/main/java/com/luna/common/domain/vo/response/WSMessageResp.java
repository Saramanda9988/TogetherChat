package com.luna.common.domain.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSMessageResp {

    @Schema(description = "消息详情")
    @NotNull
    private Object message;

    @Schema(description = "消息标记")
    private List<Object> messageMark;
}
