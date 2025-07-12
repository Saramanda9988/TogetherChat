package com.luna.webrtcserver.call.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.webrtcserver.call.domain.entity.Session;
import com.luna.webrtcserver.call.mapper.SessionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionDao extends ServiceImpl<SessionMapper, Session> {


}
