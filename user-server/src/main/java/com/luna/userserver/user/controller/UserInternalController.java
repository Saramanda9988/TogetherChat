package com.luna.userserver.user.controller;

import com.luna.common.domain.vo.request.CreateUserRequest;
import com.luna.common.domain.vo.response.ApiResult;
import com.luna.common.domain.dto.UserDTO;
import com.luna.userserver.user.domain.entity.User;
import com.luna.userserver.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务内部接口 - 仅供其他微服务调用
 */
@RestController
@RequestMapping("/user/internal")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "UserInternalController", description = "用户服务内部接口")
public class UserInternalController {
    
    private final UserService userService;

    @GetMapping("/findById")
    @Operation(summary = "根据用户ID查询用户")
    public ApiResult<UserDTO> findById(@RequestParam String userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ApiResult.fail();
        }
        UserDTO result = UserDTO
                .builder()
                .password(user.getPassword())
                .username(user.getUsername())
                .userId(Long.parseLong(userId))
                .build();
        return ApiResult.success(result);
    }

    /**
     * 创建新用户
     */
    @PostMapping("/internal/create")
    ApiResult<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        UserDTO result = userService.createUser(request);
        return ApiResult.success(result);
    }

    @PutMapping("/updateLastLogin")
    @Operation(summary = "更新用户最后登录时间")
    public ApiResult<Void> updateLastLoginTime(@RequestParam Long userId) {
        userService.updateLastLoginTime(userId);
        return ApiResult.success();
    }
}