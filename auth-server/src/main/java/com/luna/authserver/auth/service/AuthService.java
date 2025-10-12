package com.luna.authserver.auth.service;

import com.luna.authserver.auth.domain.request.LoginRequest;
import com.luna.authserver.auth.domain.request.RegisterRequest;
import com.luna.authserver.auth.domain.response.TokenResponse;
import com.luna.common.constant.RedisKey;
import com.luna.common.domain.dto.RequestInfo;
import com.luna.common.enums.CommonErrorEnum;
import com.luna.common.exception.BusinessException;
import com.luna.common.utils.JwtUtils;
import com.luna.common.utils.RedisUtils;
import com.luna.common.utils.RequestHolder;
import com.luna.userserver.api.dto.CreateUserRequest;
import com.luna.userserver.api.dto.UserDTO;
import com.luna.userserver.api.service.UserDubboService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    @DubboReference(group = "user-service")
    private UserDubboService userDubboService;

    /**
     * 获取客户端信息，如果请求上下文中没有则返回默认值
     */
    private RequestInfo getClientInfo() {
        RequestInfo requestInfo = RequestHolder.get();
        return RequestInfo.builder()
                .userId(requestInfo != null ? requestInfo.getUserId() : null)
                .username(requestInfo != null ? requestInfo.getUsername() : null)
                .imei(requestInfo != null ? requestInfo.getImei() : "default-imei")
                .clientType(requestInfo != null ? requestInfo.getClientType() : 1)
                .appId(1) // 默认应用ID
                .build();
    }

    /**
     * 生成用户访问token和刷新token
     */
    private TokenResponse generateUserTokens(UserDTO user, RequestInfo clientInfo) {
        RequestInfo tokenInfo = RequestInfo.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .imei(clientInfo.getImei())
                .clientType(clientInfo.getClientType())
                .appId(clientInfo.getAppId())
                .build();
        
        String accessToken = JwtUtils.generateAccessToken(tokenInfo);
        String refreshToken = JwtUtils.generateRefreshToken(tokenInfo);
        
        // 将token存储到Redis中
        String accessKey = String.format(RedisKey.ACCESS_TOKEN_KEY, user.getUserId(), UUID.randomUUID().toString());
        String refreshKey = String.format(RedisKey.REFRESH_TOKEN_KEY, user.getUserId(), UUID.randomUUID().toString());
        
        // 存储token到Redis，设置过期时间
        RedisUtils.set(accessKey, accessToken, RedisKey.ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        RedisUtils.set(refreshKey, refreshToken, RedisKey.REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        
        // 存储token键到用户token集合中，方便后续管理
        String userTokenKey = String.format(RedisKey.USER_TOKEN_SET_KEY, user.getUserId());
        RedisUtils.sSetAndTime(userTokenKey, RedisKey.REFRESH_TOKEN_EXPIRE_TIME, accessKey, refreshKey);
        
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 验证用户密码
     */
    private boolean validatePassword(String inputPassword, String storedPassword) {
        return Objects.equals(inputPassword, storedPassword);
    }

    public void logout() {
        // 在实际项目中，这里可以：
        // 1. 将token加入黑名单
        // 2. 清除Redis中的用户会话
        // 3. 记录登出日志
        RequestInfo requestInfo = RequestHolder.get();
        if (requestInfo != null && requestInfo.getUserId() != null) {
            log.info("用户登出: userId={}, username={}, imei={}",
                    requestInfo.getUserId(), requestInfo.getUsername(), requestInfo.getImei());

            // 将用户的所有token加入黑名单
            String userTokenKey = String.format(RedisKey.USER_TOKEN_SET_KEY, requestInfo.getUserId());
            // 获取用户所有的token键
            java.util.Set<String> tokenKeys = RedisUtils.sGet(userTokenKey);
            if (tokenKeys != null) {
                // 将所有token加入黑名单
                tokenKeys.forEach(tokenKey -> {
                    String token = RedisUtils.getStr(tokenKey);
                    if (token != null) {
                        // 将token加入黑名单，设置与原token相同的过期时间
                        Long expireTime = RedisUtils.getExpire(tokenKey, TimeUnit.MILLISECONDS);
                        if (expireTime > 0) {
                            RedisUtils.set(
                                String.format(RedisKey.TOKEN_BLACKLIST_KEY, token),
                                "1",
                                expireTime,
                                TimeUnit.MILLISECONDS
                            );
                        }
                    }
                });
                
                // 删除用户的token集合
                RedisUtils.del(userTokenKey);
            }

            // TODO: 清除用户会话
            // sessionService.removeUserSession(requestInfo.getUserId());

        } else {
            log.warn("用户登出时未找到有效的用户信息");
        }
    }

    public TokenResponse login(LoginRequest userLoginRequest) {
        // 参数校验
        if (userLoginRequest == null || userLoginRequest.getUserId() == null || userLoginRequest.getPassword() == null) {
            throw new BusinessException("登录参数不能为空");
        }

        // 验证用户凭据
        UserDTO user = userDubboService.getUserById(userLoginRequest.getUserId());
        if (Objects.isNull(user)) {
            log.warn("用户不存在: userId={}", userLoginRequest.getUserId());
            throw new BusinessException(CommonErrorEnum.USER_NOT_EXIST);
        }

        // 验证密码
        if (!validatePassword(userLoginRequest.getPassword(), user.getPassword())) {
            log.warn("密码错误: userId={}", userLoginRequest.getUserId());
            throw new BusinessException("用户名或密码错误");
        }

        // 获取客户端信息并生成token
        RequestInfo clientInfo = getClientInfo();
        TokenResponse tokens = generateUserTokens(user, clientInfo);

        log.info("用户登录成功: userId={}, username={}", user.getUserId(), user.getUsername());
        return tokens;
    }

    public TokenResponse register(RegisterRequest registerRequest) {
        // 参数校验
        if (registerRequest == null || registerRequest.getName() == null || registerRequest.getPassword() == null) {
            throw new BusinessException("注册参数不能为空");
        }

        // 创建用户
        UserDTO user = userDubboService.createUser(CreateUserRequest
                .builder()
                .password(registerRequest.getPassword())
                .username(registerRequest.getName())
                .build());

        if (Objects.isNull(user)) {
            log.error("用户创建失败: username={}", registerRequest.getName());
            throw new BusinessException("注册失败");
        }

        // 获取客户端信息并生成token
        RequestInfo clientInfo = getClientInfo();
        TokenResponse tokens = generateUserTokens(user, clientInfo);

        log.info("用户注册成功: userId={}, username={}", user.getUserId(), user.getUsername());
        return tokens;
    }
    
    /**
     * 使用刷新token获取新的访问token
     * 
     * @param refreshToken 刷新token
     * @return 新的双token
     */
    public TokenResponse refreshToken(String refreshToken) {
        try {
            // 检查token是否在黑名单中
            if (RedisUtils.hasKey(String.format(RedisKey.TOKEN_BLACKLIST_KEY, refreshToken))) {
                throw new BusinessException("refresh token已被注销");
            }
            
            RequestInfo requestInfo = JwtUtils.parseJwtToken(refreshToken);
            if (requestInfo == null || requestInfo.getUserId() == null) {
                throw new BusinessException("refresh token无效");
            }
            
            // 验证用户是否存在
            UserDTO user = userDubboService.getUserById(String.valueOf(requestInfo.getUserId()));
            if (Objects.isNull(user)) {
                log.warn("用户不存在: userId={}", requestInfo.getUserId());
                throw new BusinessException(CommonErrorEnum.USER_NOT_EXIST);
            }
            
            // 生成新的双token
            TokenResponse tokens = generateUserTokens(user, requestInfo);
            log.info("刷新token成功: userId={}", user.getUserId());
            return tokens;
        } catch (Exception e) {
            log.warn("refresh token解析失败: {}", e.getMessage());
            throw new BusinessException("refresh token无效");
        }
    }
}