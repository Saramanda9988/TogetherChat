package com.luna.authserver.auth.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "登录信息响应对象")
public class LoginInfoVO {
    @NotNull
    @Schema(description = "登录的用户ID")
    private Long userId;

    @NotNull
    @Schema(description = "登录的用户名")
    private String name;
}
