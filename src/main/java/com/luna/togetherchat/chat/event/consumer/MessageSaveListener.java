package com.luna.togetherchat.chat.event.consumer;

import com.luna.togetherchat.chat.dao.MessageDao;
import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.common.constant.MQConstant;
import com.luna.togetherchat.websocket.domain.dto.PushMessageDTO;
import com.luna.togetherchat.websocket.domain.enums.WSPushTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageSaveListener {
    @Resource
    private MessageDao messageDao;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MQConstant.MESSAGE_SAVE_QUEUE, durable = "true"),
                    exchange = @Exchange(value = MQConstant.MESSAGE_SAVE_EXCHANGE, type = "topic"),
                    key = "message.save.#" // 监听所有以 message.save. 开头的路由键
            )
    )
    public void onMessage(Message message) {
        messageDao.save(message);
    }
}
