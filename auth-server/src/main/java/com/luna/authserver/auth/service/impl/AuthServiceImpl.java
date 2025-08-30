package com.luna.authserver.auth.service.impl;

import com.luna.authserver.auth.domain.request.LoginRequest;
import com.luna.authserver.auth.domain.request.RegisterRequest;
import com.luna.authserver.auth.service.AuthService;
import com.luna.common.enums.CommonErrorEnum;
import com.luna.common.enums.UserErrorEnum;
import com.luna.common.exception.BusinessException;
import com.luna.userserver.api.dto.CreateUserRequest;
import com.luna.userserver.api.dto.UserDTO;
import com.luna.userserver.api.service.UserDubboService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @DubboReference(group = "user-service")
    private UserDubboService userDubboService;

    @Override
    public void logout() {

    }

    @Override
    public String login(LoginRequest userLoginRequest) {
        UserDTO result = userDubboService.getUserById(userLoginRequest.getUserId());
        if (Objects.isNull(result)) {
            throw new BusinessException(CommonErrorEnum.USER_NOT_EXIST);
        }
        if (Objects.equals(result.getPassword(), userLoginRequest.getPassword())) {
            return result.getUserId().toString();
        }
        return null;
    }

    @Override
    public String register(RegisterRequest registerRequest) {
        UserDTO result = userDubboService.createUser(CreateUserRequest
                .builder()
                .password(registerRequest.getPassword())
                .username(registerRequest.getName())
                .build());
        if (Objects.isNull(result)) {
            throw new BusinessException(UserErrorEnum.USER_NAME_EXIST);
        }
        return result.getUserId().toString();
    }
}
