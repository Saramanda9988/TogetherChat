package com.luna.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketHealthVO {
    private String status;

    private Integer totalInstances;

    private Integer healthyInstances;

    private Long discoveredAt;
}
