package com.luna.togetherchat.chat.service.strategy.flink.consumer;

import com.luna.togetherchat.chat.service.strategy.flink.SinkFactory;
import com.luna.togetherchat.chat.service.strategy.flink.sink.AbstractSink;
import com.luna.togetherchat.chat.service.strategy.flink.sink.entity.Change;
import com.luna.togetherchat.common.constant.MQConstant;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SqlChangeConsumer {

    @RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(value = MQConstant.MYSQL_CHANGE_QUEUE, durable = "true"),
                exchange = @Exchange(value = MQConstant.MYSQL_CHANGE_EXCHANGE),
                key = "db.change"
        )
    )
    public void onMessage(Change message) {
        AbstractSink strategyNoNull = SinkFactory.getStrategyNoNull(message.getTableName());
        strategyNoNull.process(message);
    }
}

