package com.luna.userserver.user.service;

import com.luna.userserver.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luna.userserver.user.domain.request.UserLoginRequest;
import com.luna.userserver.user.domain.request.UserRegisterRequest;
import com.luna.userserver.user.domain.response.LoginInfoResponse;
import com.luna.userserver.user.domain.response.UserInfoResponse;
import jakarta.servlet.http.HttpServletResponse;

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

    void logout(String refreshToken);

    LoginInfoResponse login(UserLoginRequest userLoginRequest, HttpServletResponse response);

    String refreshToken(String refreshToken, HttpServletResponse response);

    void register(UserRegisterRequest registerRequest);
}
