package com.luna.common.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "创建用户请求对象")
public class CreateUserRequest {
    @Schema(description = "用户名", example = "john_doe")
    private String username;

    @Schema(description = "密码", example = "password123")
    private String password;
}
