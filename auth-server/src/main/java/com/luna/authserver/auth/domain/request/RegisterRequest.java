package com.luna.authserver.auth.domain.request;

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
@Schema(description = "注册请求参数")
public class RegisterRequest {
    @NotNull
    @Schema(description = "注册的用户名")
    private String name;

    @NotNull
    @Schema(description = "注册的密码")
    private String password;
}
