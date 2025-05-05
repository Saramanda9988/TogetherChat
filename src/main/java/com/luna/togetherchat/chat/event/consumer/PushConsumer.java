package com.luna.togetherchat.chat.event.consumer;


import com.luna.togetherchat.common.constant.MQConstant;
import com.luna.togetherchat.websocket.domain.dto.PushMessageDTO;
import com.luna.togetherchat.websocket.domain.enums.WSPushTypeEnum;
import com.luna.togetherchat.websocket.service.WebSocketService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-12
 */
@Component
public class PushConsumer {
    @Autowired
    private WebSocketService webSocketService;

    @RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(value = MQConstant.PUSH_QUEUE, durable = "true"),
                exchange = @Exchange(value = MQConstant.PUSH_EXCHANGE, type = "topic"),
                key = "*"
        )
     )
    public void onMessage(PushMessageDTO message) {
        WSPushTypeEnum wsPushTypeEnum = WSPushTypeEnum.of(message.getPushType());
        switch (wsPushTypeEnum) {
            case USER:
                message.getUidList().forEach(uid -> {
                    webSocketService.sendToUid(message.getWsBaseMsg(), uid);
                });
                break;
            case ALL:
                webSocketService.sendToAllOnline(message.getWsBaseMsg(), null);
                break;
        }
    }
}

