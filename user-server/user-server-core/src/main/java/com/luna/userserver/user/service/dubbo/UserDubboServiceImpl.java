package com.luna.userserver.user.service.dubbo;

import com.luna.userserver.api.dto.CreateUserRequest;
import com.luna.userserver.api.dto.UserDTO;
import com.luna.userserver.api.service.UserDubboService;
import com.luna.userserver.user.dao.UserDao;
import com.luna.userserver.user.domain.entity.User;
import com.luna.userserver.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 用户服务Dubbo实现类
 * 提供用户相关的远程调用服务实现
 *
 * @author LunaRain_079
 * @since 2025-08-30
 */
@Slf4j
@Component
@AllArgsConstructor
@DubboService(group = "user-service", interfaceClass = UserDubboService.class) //TODO: 或许不需要进行分组
@Service
public class UserDubboServiceImpl implements UserDubboService {

    private final UserDao userDao;

    private final UserService userService;

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public UserDTO getUserById(String userId) {
        User user = userDao.getById(userId);
        if (Objects.isNull(user)) {
            return null;
        }
        return UserDTO.builder()
                .username(user.getUsername())
                .userId(user.getUserId())
                .password(user.getPassword())
                .build();
    }

    /**
     * 创建新用户
     *
     * @param request 创建用户请求对象
     * @return 创建的用户信息
     */
    @Override
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        User user = User
                .builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        User byName = userDao.getByName(request.getUsername());
        if (byName != null) {
            return null;
        }
        userDao.save(user);
        return UserDTO
                .builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}