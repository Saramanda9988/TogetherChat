package com.luna.togetherchat.chat.dao;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.togetherchat.chat.domain.entity.MessageMark;
import com.luna.togetherchat.chat.mapper.MessageMarkMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息标记表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-04-08
 */
@Service
public class MessageMarkDao extends ServiceImpl<MessageMarkMapper, MessageMark> {

}
