package com.luna.userserver.user.controller;

import com.luna.common.constant.Const;
import com.luna.common.domain.vo.response.ApiResult;
import com.luna.common.utils.RequestHolder;
import com.luna.userserver.user.domain.entity.User;
import com.luna.userserver.user.domain.request.UserLoginRequest;
import com.luna.userserver.user.domain.request.UserRegisterRequest;
import com.luna.userserver.user.domain.response.LoginInfoResponse;
import com.luna.userserver.user.domain.response.UserInfoResponse;
import com.luna.userserver.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
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

    @PutMapping("/info")
    @Operation(summary = "修改用户信息")
    public ApiResult<UserInfoResponse> updateUserInfo(@RequestBody UserInfoResponse userInfoResponse) {
        userService.updateUserInfo(userInfoResponse);
        return ApiResult.success();
    }
}
