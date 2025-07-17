package com.luna.websocketserver.websocket.consumer;


import com.luna.common.constant.MQConstant;
import com.luna.websocketserver.websocket.domain.dto.PushMessageDTO;
import com.luna.websocketserver.websocket.enums.WSPushTypeEnum;
import com.luna.websocketserver.websocket.service.WebSocketService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
        List<Long> onlineUserIds = webSocketService.getOnlineUserId();

        if (message.getUidList() == null || message.getUidList().isEmpty()) {
            return;
        }

        List<Long> uidList = message.getUidList()
                .stream()
                .filter(onlineUserIds::contains)
                .toList();
        switch (wsPushTypeEnum) {
            case USER:
                uidList.forEach(uid -> {
                    webSocketService.sendToUid(message.getWsBaseMsg(), uid);
                });
                break;
            case ALL:
                webSocketService.sendToAllOnline(message.getWsBaseMsg(), null);
                break;
        }
    }
}

