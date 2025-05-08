package com.luna.togetherchat.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.togetherchat.user.domain.entity.User;
import com.luna.togetherchat.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserDao extends ServiceImpl<UserMapper, User> {
}
