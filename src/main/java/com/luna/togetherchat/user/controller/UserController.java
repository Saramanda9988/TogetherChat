package com.luna.togetherchat.user.controller;

import com.luna.togetherchat.common.constant.Const;
import com.luna.togetherchat.common.domain.vo.response.ApiResult;
import com.luna.togetherchat.common.utils.RequestHolder;
import com.luna.togetherchat.user.domain.request.UserLoginRequest;
import com.luna.togetherchat.user.domain.request.UserRegisterRequest;
import com.luna.togetherchat.user.domain.response.LoginInfoResponse;
import com.luna.togetherchat.user.domain.response.UserInfoResponse;
import com.luna.togetherchat.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/capi/user")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "UserController", description = "用户接口")
public class UserController {
    private final UserService userService;

    @GetMapping("info/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前用户信息")
    public ApiResult<UserInfoResponse> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUserId()));
    }

    @GetMapping("info/{id}")
    @Operation(summary = "获取指定用户信息", description = "获取指定用户信息")
    public ApiResult<UserInfoResponse> getUserInfo(@PathVariable Long id) {
        return ApiResult.success(userService.getUserInfo(id));
    }

    @GetMapping("logout")
    @Operation(summary = "退出登录", description = "退出登录")
    public ApiResult<Void> logout(@CookieValue(value = Const.REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {
        userService.logout(refreshToken);
        return ApiResult.success();
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口 注意：后端返回的 token 不携带Bearer前缀")
    public ApiResult<LoginInfoResponse> login(@RequestBody @Valid UserLoginRequest userLoginRequest,
                                              HttpServletResponse response) {
        return ApiResult.success(userService.login(userLoginRequest, response));
    }

    /**
     * "@CookieValue"注解会自动从请求的Cookie中提取refresh_token的值
     * Spring MVC会自动创建HttpServletResponse对象并注入到方法参数中
     * 这是Spring框架的依赖注入特性，对前端完全透明
     * 浏览器会自动发送相关Cookie
     * 服务器设置的新Cookie会自动被浏览器保存
     * @param refreshToken
     * @param response
     * @return
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新token", description = "刷新token接口 注意：后端返回的 token 不携带Bearer前缀")
    public ApiResult<String> refreshToken(@CookieValue(value = Const.REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
                                          HttpServletResponse response) {
        return ApiResult.success(userService.refreshToken(refreshToken, response));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口")
    public ApiResult<Void> register(@RequestBody @Valid UserRegisterRequest registerRequest) {
        userService.register(registerRequest);
        return ApiResult.success();
    }
}
