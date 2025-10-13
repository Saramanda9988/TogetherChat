package com.luna.messageserver.message.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.messageserver.message.domain.entity.Message;
import com.luna.messageserver.message.mapper.MessageMapper;
import org.springframework.stereotype.Service;

@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {

}
