package com.luna.authserver.auth.feign;

import com.luna.common.domain.vo.request.CreateUserRequest;
import com.luna.common.domain.vo.response.ApiResult;
import com.luna.common.domain.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "user-server",  // 服务名，与Nacos注册的服务名一致
        path = "/user"         // 基础路径
)
public interface UserServiceClient {

    /**
     * 根据用户ID查询用户
     */
    @GetMapping("/internal/findById")
    ApiResult<UserDTO> findById(@RequestParam("userId") String userId);

    /**
     * 创建新用户
     */
    @PostMapping("/internal/create")
    ApiResult<UserDTO> createUser(@RequestBody CreateUserRequest request);

    /**
     * 更新用户最近上线时间
     */
    @PutMapping("/updateLastLogin")
    ApiResult<Void> updateLastLoginTime(@RequestParam Long userId);

}
