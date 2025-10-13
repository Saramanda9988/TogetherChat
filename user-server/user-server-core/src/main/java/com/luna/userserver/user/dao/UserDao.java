package com.luna.userserver.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.userserver.user.domain.entity.User;
import com.luna.userserver.user.mapper.UserMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public class UserDao extends ServiceImpl<UserMapper, User> {

    public User getByName(String username) {
        return lambdaQuery()
                .eq(User::getUsername, username)
                .one();
    }
}
