package com.luna.userserver.user.service;

import com.luna.userserver.user.dao.UserDao;
import com.luna.userserver.user.domain.entity.User;
import com.luna.userserver.user.domain.response.UserInfoResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class UserService {

    private final UserDao userDao;

    public UserInfoResponse getUserInfo(Long userId) {
        User user = userDao.getById(userId);
        return UserInfoResponse
                .builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .activeStatus(user.getStatus())
                .build();
    }

    @Transactional
    public void updateUserInfo(UserInfoResponse userInfoResponse) {
        userDao.updateById(new User(userInfoResponse));
    }
}
