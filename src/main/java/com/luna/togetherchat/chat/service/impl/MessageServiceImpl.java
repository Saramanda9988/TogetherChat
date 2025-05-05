package com.luna.togetherchat.chat.service.impl;

import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.chat.mapper.MessageMapper;
import com.luna.togetherchat.chat.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

}
