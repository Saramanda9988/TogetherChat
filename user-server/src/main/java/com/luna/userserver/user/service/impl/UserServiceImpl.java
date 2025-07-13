package com.luna.userserver.user.service.impl;

import com.luna.common.domain.vo.request.CreateUserRequest;
import com.luna.common.domain.dto.UserDTO;
import com.luna.common.exception.BusinessException;
import com.luna.userserver.user.dao.UserDao;
import com.luna.userserver.user.domain.entity.User;
import com.luna.userserver.user.domain.response.UserInfoResponse;
import com.luna.common.enums.UserErrorEnum;
import com.luna.userserver.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
        User user = userDao.getById(userId);
        return UserInfoResponse
                .builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .activeStatus(user.getStatus())
                .build();
    }

    @Override
    public User getUserById(String userId) {
        return userDao.getById(userId);
    }



    @Override
    public void updateLastLoginTime(Long userId) {
        User user = getUserById(String.valueOf(userId));
        if (user == null) {
            throw new BusinessException(UserErrorEnum.USER_NOT_EXIST);
        }
        user.setUpdatedAt(LocalDateTime.now());
        userDao.updateById(user);
    }

    @Override
    public UserDTO createUser(CreateUserRequest request) {
        User user = User
                .builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        User byName = userDao.getByName(request.getUsername());
        if (byName != null) {
            throw new BusinessException(UserErrorEnum.USER_NAME_EXIST);
        }
        userDao.save(user);
        return UserDTO
                .builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    @Override
    public void updateUserInfo(UserInfoResponse userInfoResponse) {
        userDao.updateById(new User(userInfoResponse));
    }
}
