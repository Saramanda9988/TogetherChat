package com.luna.togetherchat.chat.event.consumer;

import com.luna.togetherchat.chat.dao.MessageDao;
import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.common.constant.MQConstant;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageChangeConsumer {
    @Resource
    private MessageDao messageDao;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MQConstant.MESSAGE_CHANGE_QUEUE, durable = "true"),
                    exchange = @Exchange(value = MQConstant.MESSAGE_CHANGE_EXCHANGE, type = "topic"),
                    key = MQConstant.MESSAGE_CHANGE_ROUTING_KEY // 监听所有以 message.change. 开头的路由键
            )
    )
    public void onMessage(@Valid Message message) {
        messageDao.updateById(message);
    }
}