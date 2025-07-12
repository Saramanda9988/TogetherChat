package com.luna.chatserver.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.chatserver.chat.domain.entity.MessageMark;
import com.luna.chatserver.chat.mapper.MessageMarkMapper;
import com.luna.chatserver.chat.service.MessageMarkService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author starrybamboo
 * @since 2024-09-03
 */
@Service
public class MessageMarkServiceImp extends ServiceImpl<MessageMarkMapper, MessageMark> implements MessageMarkService {

}
