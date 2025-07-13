package com.luna.authserver.auth.service;

import com.luna.authserver.auth.domain.request.LoginRequest;
import com.luna.authserver.auth.domain.request.RegisterRequest;
import com.luna.authserver.auth.domain.response.LoginInfoVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;


public interface AuthService {
    void logout();

    String login(LoginRequest userLoginRequest);

    String register(RegisterRequest registerRequest);
}
