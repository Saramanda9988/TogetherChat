package com.luna.chatserver.chat.event.consumer;

import com.luna.chatserver.chat.dao.MessageDao;
import com.luna.chatserver.chat.domain.entity.Message;
import com.luna.common.constant.MQConstant;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageSaveConsumer {
    @Resource
    private MessageDao messageDao;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MQConstant.MESSAGE_SAVE_QUEUE, durable = "true"),
                    exchange = @Exchange(value = MQConstant.MESSAGE_SAVE_EXCHANGE, type = "topic"),
                    key = MQConstant.MESSAGE_SAVE_ROUTING_KEY // 监听所有以 message.save. 开头的路由键
            )
    )
    public void onMessage(Message message) {
        messageDao.save(message);
    }
}
