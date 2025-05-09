package com.luna.togetherchat.user.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "登录的用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "登录的用户密码，进行了加密传输")
    private String password;
}
