package com.luna.togetherchat.user.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.luna.togetherchat.common.constant.Const;
import com.luna.togetherchat.common.constant.RedisKey;
import com.luna.togetherchat.common.domain.dto.RequestInfo;
import com.luna.togetherchat.common.enums.CommonErrorEnum;
import com.luna.togetherchat.common.exception.BusinessException;
import com.luna.togetherchat.common.utils.JwtUtils;
import com.luna.togetherchat.common.utils.RedisUtils;
import com.luna.togetherchat.user.dao.UserDao;
import com.luna.togetherchat.user.domain.entity.User;
import com.luna.togetherchat.user.domain.request.UserLoginRequest;
import com.luna.togetherchat.user.domain.request.UserRegisterRequest;
import com.luna.togetherchat.user.domain.response.LoginInfoResponse;
import com.luna.togetherchat.user.domain.response.UserInfoResponse;
import com.luna.togetherchat.user.enums.UserErrorEnum;
import com.luna.togetherchat.user.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.togetherchat.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        return null;
    }

    @Override
    public void logout(String refreshToken) {

    }

    @Override
    public LoginInfoResponse login(UserLoginRequest userLoginRequest, HttpServletResponse response) {
        // 检查有没有这个用户
        User user = userDao.getByName(userLoginRequest.getUsername());
        if (Objects.isNull(user)) {
            throw new BusinessException(UserErrorEnum.USER_NOT_EXIST);
        }

        // 检查密码是否正确
        if (!BCrypt.checkpw(userLoginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(UserErrorEnum.LOGIN_PASSWORD_ERROR);
        }

        // 生成双token
        RequestInfo requestInfo = RequestInfo.builder()
                .username(user.getUsername())
                .userId(user.getUserId())
                .build();
        String accessToken = getAccessTokenAndRefresh(response, requestInfo);

        return LoginInfoResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .activeStatus(user.getStatus())
                .lastLoginTime(LocalDateTime.now())
                .accessToken(accessToken)
                .build();
    }

    /**
     * 生成 accessToken 和 refreshToken，并写入 redis 和 cookie
     *
     * @param response    HttpServletResponse
     * @param requestInfo RequestInfo
     * @return String accessToken
     */
    private static String getAccessTokenAndRefresh(HttpServletResponse response, RequestInfo requestInfo) {
        String accessToken = JwtUtils.generateAccessToken(requestInfo);
        // 2.2 refreshToken
        String refreshTokenKey = String.valueOf(UUID.randomUUID());
        // 2.3 存入 redis
        RedisUtils.set(RedisKey.getKey(RedisKey.REFRESH_TOKEN, refreshTokenKey), requestInfo, Const.REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        // 2.4 写入 cookie
        Cookie cookie = new Cookie(Const.REFRESH_TOKEN_COOKIE_NAME, refreshTokenKey);
        cookie.setHttpOnly(true); // 防止通过js获取cookie
        cookie.setMaxAge((int) (Const.REFRESH_TOKEN_EXPIRE_TIME / 1000)); // 毫秒转秒
        cookie.setPath("/");
        cookie.setSecure(false);
        response.addCookie(cookie);
        return accessToken;
    }

    @Override
    public String refreshToken(String refreshToken, HttpServletResponse response) {
        // 1. 判断 refreshToken 是否存在
        if (Objects.isNull(refreshToken)) {
            throw new BusinessException(CommonErrorEnum.REFRESH_TOKEN_INVALID);
        }
        // 2. 从redis中获取 requestInfo
        RequestInfo requestInfo = RedisUtils.getDel(RedisKey.getKey(RedisKey.REFRESH_TOKEN, refreshToken), RequestInfo.class);
        // 3. 判断 requestInfo 是否存在
        if (Objects.isNull(requestInfo)) {
            throw new BusinessException(CommonErrorEnum.REFRESH_TOKEN_INVALID);
        }
        // 4. 生成新的 accessToken
        String accessToken = getAccessTokenAndRefresh(response, requestInfo);
        // 5. 返回新的 accessToken
        return accessToken;
    }

    @Override
    public void register(UserRegisterRequest registerRequest) {
        User user = userDao.getByName(registerRequest.getUsername());
        if(Objects.nonNull(user)) {
            throw new BusinessException(UserErrorEnum.USER_NAME_EXIST);
        }
        userDao.save(User.builder()
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .build());
    }
}
