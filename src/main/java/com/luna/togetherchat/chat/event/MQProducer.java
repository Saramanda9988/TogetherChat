package com.luna.togetherchat.chat.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Description: 发送mq工具类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-12
 */
public class MQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMsg(String exchange, Object body) {
        rabbitTemplate.convertAndSend(exchange,"order", body);
    }

    public void sendDbChange(String exchange, Object body) {
        rabbitTemplate.convertAndSend(exchange,"123", body);
    }

}
