package com.luna.userserver.api.service;

import com.luna.userserver.api.dto.CreateUserRequest;
import com.luna.userserver.api.dto.UserDTO;

/**
 * 用户服务Dubbo接口定义
 * 提供用户相关的远程调用服务
 */
public interface UserDubboService {

    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserDTO getUserById(String userId);

    /**
     * 创建新用户
     * @param request 创建用户请求对象
     * @return 创建的用户信息
     */
    UserDTO createUser(CreateUserRequest request);
}