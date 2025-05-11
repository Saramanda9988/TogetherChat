package com.luna.togetherchat.call.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.togetherchat.call.domain.entity.Session;
import com.luna.togetherchat.call.mapper.SessionMapper;
import org.springframework.stereotype.Service;

@Service
public class SessionDao extends ServiceImpl<SessionMapper, Session> {
}
