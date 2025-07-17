package com.luna.common.utils;

import com.luna.common.constant.MQConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 发送mq工具类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-12
 */
@Component
public class MQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMsg(String exchange, Object body) {
        rabbitTemplate.convertAndSend(exchange,"order", body);
    }

    public void sendDbChange(String exchange, Object body) {
        rabbitTemplate.convertAndSend(exchange, MQConstant.MESSAGE_CHANGE_KEY, body);
    }

    public void saveMsg(String exchange, Object body) {
        rabbitTemplate.convertAndSend(exchange, MQConstant.MESSAGE_SAVE_KEY, body);
    }
}
