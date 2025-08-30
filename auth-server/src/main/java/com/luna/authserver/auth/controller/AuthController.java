package com.luna.authserver.auth.controller;

import com.luna.authserver.auth.domain.request.LoginRequest;
import com.luna.authserver.auth.domain.request.RegisterRequest;
import com.luna.authserver.auth.service.AuthService;
import com.luna.common.domain.vo.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "验证服务api")
@RequestMapping("/auth")
public class AuthController {

     private final AuthService authService;

     @GetMapping("/logout")
     @Operation(summary = "退出登录", description = "退出登录")
     public ApiResult<Void> logout() {
          authService.logout();
          return ApiResult.success();
     }

     @PostMapping("/login")
     @Operation(summary = "用户登录", description = "用户登录接口 注意：后端返回的 token 不携带Bearer前缀")
     public ApiResult<String> login(@RequestBody @Valid LoginRequest userLoginRequest) {
          String token = authService.login(userLoginRequest);
          return token == null
               ? ApiResult.fail()
               : ApiResult.success(token);
     }

     @PostMapping("/register")
     @Operation(summary = "用户注册", description = "用户注册接口")
     public ApiResult<String> register(@RequestBody @Valid RegisterRequest registerRequest) {
          String token = authService.register(registerRequest);
          return ApiResult.success(token);
     }
}
