package com.luna.togetherchat.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.chat.mapper.MessageMapper;
import com.luna.togetherchat.chat.service.MessageService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author starrybamboo
 * @since 2025-03-26
 */
@Service
public class MessageServiceImp extends ServiceImpl<MessageMapper, Message> implements MessageService {

}
