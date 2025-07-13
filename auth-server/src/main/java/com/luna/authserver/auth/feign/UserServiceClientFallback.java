package com.luna.authserver.auth.feign;

import com.luna.common.domain.vo.request.CreateUserRequest;
import com.luna.common.domain.vo.response.ApiResult;
import com.luna.common.domain.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@Slf4j
public class UserServiceClientFallback implements UserServiceClient {
    @Override
    public ApiResult<UserDTO> findById(String userId) {
        log.error("调用user-server查询用户失败，userId: {}", userId);
        return ApiResult.fail();
    }

    @Override
    public ApiResult<UserDTO> createUser(CreateUserRequest request) {
        log.error("调用user-server新建用户失败，userName: {}", request.getUsername());
        return ApiResult.fail();
    }

    @PutMapping("/updateLastLogin")
    @Operation(summary = "更新用户最后登录时间")
    public ApiResult<Void> updateLastLoginTime(@RequestParam Long userId) {
        log.error("调用user-server更新用户最后登录时间失败，userId: {}", userId);
        return ApiResult.fail();
    }

}