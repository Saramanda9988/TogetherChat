package com.luna.userserver.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Max(value = 10, message = "用户名长度不能超过10个字符")
    @Min(value = 5, message = "用户名长度不能少于5个字符")
    private String username;

    @Max(value = 20, message = "密码长度不能超过20个字符")
    @Min(value = 8, message = "密码长度不能少于8个字符")
    private String password;
}