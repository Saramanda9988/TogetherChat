package com.luna.togetherchat.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.togetherchat.user.domain.entity.User;
import com.luna.togetherchat.user.mapper.UserMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;

@Service
public class UserDao extends ServiceImpl<UserMapper, User> {

    public User getByName(String username) {
        return lambdaQuery()
                .eq(User::getUsername, username)
                .one();
    }
}
