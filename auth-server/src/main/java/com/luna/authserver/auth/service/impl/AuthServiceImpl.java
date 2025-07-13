package com.luna.authserver.auth.service.impl;

import com.luna.authserver.auth.domain.request.LoginRequest;
import com.luna.authserver.auth.domain.request.RegisterRequest;
import com.luna.authserver.auth.domain.response.LoginInfoVO;
import com.luna.authserver.auth.feign.UserServiceClient;
import com.luna.authserver.auth.service.AuthService;
import com.luna.common.constant.Const;
import com.luna.common.constant.RedisKey;
import com.luna.common.domain.dto.RequestInfo;
import com.luna.common.domain.dto.UserDTO;
import com.luna.common.domain.vo.request.CreateUserRequest;
import com.luna.common.domain.vo.response.ApiResult;
import com.luna.common.enums.CommonErrorEnum;
import com.luna.common.enums.UserErrorEnum;
import com.luna.common.exception.BusinessException;
import com.luna.common.utils.JwtUtils;
import com.luna.common.utils.RedisUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserServiceClient userServiceClient;

    @Override
    public void logout() {

    }

    @Override
    public String login(LoginRequest userLoginRequest) {
        ApiResult<UserDTO> result = userServiceClient.findById(userLoginRequest.getUserId());
        if (!result.getSuccess()) {
            throw new BusinessException(CommonErrorEnum.USER_NOT_EXIST);
        }
        if (Objects.equals(result.getData().getPassword(), userLoginRequest.getPassword())) {
            return result.getData().getUserId().toString();
        }
        return null;
    }

    @Override
    public String register(RegisterRequest registerRequest) {
        ApiResult<UserDTO> result = userServiceClient.createUser(CreateUserRequest
                .builder()
                .password(registerRequest.getPassword())
                .username(registerRequest.getName())
                .build());
        if (!result.getSuccess()) {
            if (Objects.equals(result.getErrCode(), UserErrorEnum.USER_NAME_EXIST.getErrorCode())) {
                throw new BusinessException(UserErrorEnum.USER_NAME_EXIST);
            } else {
                throw new BusinessException(CommonErrorEnum.SYSTEM_ERROR);
            }
        }
        return result.getData().getUserId().toString();
    }
}
