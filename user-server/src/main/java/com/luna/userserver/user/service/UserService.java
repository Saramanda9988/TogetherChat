package com.luna.userserver.user.service;

import com.luna.common.domain.vo.request.CreateUserRequest;
import com.luna.common.domain.dto.UserDTO;
import com.luna.userserver.user.domain.entity.User;
import com.luna.userserver.user.domain.response.UserInfoResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
public interface UserService {

    UserInfoResponse getUserInfo(Long userId);

    void updateUserInfo(UserInfoResponse userInfoResponse);

    User getUserById(String userId);

    void updateLastLoginTime(Long userId);

    UserDTO createUser(CreateUserRequest request);
}
