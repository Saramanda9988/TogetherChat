package com.luna.authserver.auth.controller;

import com.luna.authserver.auth.domain.request.LoginRequest;
import com.luna.authserver.auth.domain.request.RegisterRequest;
import com.luna.authserver.auth.domain.response.TokenResponse;
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
     public ApiResult<TokenResponse> login(@RequestBody @Valid LoginRequest userLoginRequest) {
          TokenResponse tokens = authService.login(userLoginRequest);
          return tokens == null
               ? ApiResult.fail(10001, "登录失败")
               : ApiResult.success(tokens);
     }
     
     @PostMapping("/register")
     @Operation(summary = "用户注册", description = "用户注册接口")
     public ApiResult<TokenResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
          TokenResponse tokens = authService.register(registerRequest);
          return tokens == null
               ? ApiResult.fail(10001, "注册失败")
               : ApiResult.success(tokens);
     }
     
     @PostMapping("/refresh")
     @Operation(summary = "刷新token", description = "使用refreshToken换取新的双token")
     public ApiResult<TokenResponse> refreshToken(@RequestHeader("refresh-token") String refreshToken) {
          TokenResponse tokens = authService.refreshToken(refreshToken);
          return tokens == null
               ? ApiResult.fail(10001, "刷新token失败")
               : ApiResult.success(tokens);
     }
}