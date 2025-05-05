package com.luna.togetherchat.websocket.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSLoginSuccess {
    @Schema(description = "用户ID")
    private Long uid;
    @Schema(description = "用户头像")
    private String avatar;
    @Schema(description = "用户令牌")
    private String token;
    @Schema(description = "用户名称")
    private String name;
    @Schema(description = "用户权限 0普通用户 1超管")
    private Integer power;
}
