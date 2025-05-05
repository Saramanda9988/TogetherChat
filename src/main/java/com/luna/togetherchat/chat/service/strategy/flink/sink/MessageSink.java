package com.luna.togetherchat.chat.service.strategy.flink.sink;

import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.chat.service.strategy.flink.sink.entity.Change;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageSink extends AbstractSink<Message> {

    @Value("${tuanchat.message.table.name}")
    private  String message;

    @Override
    public String getTableName() {
        return message;
    }

    @Override
    public void processInsert(Change change, Message after) {
        
    }

    @Override
    public void processUpdate(Change change, Message before) {
    }

    @Override
    public void processDelete(Change change, Message before) {

    }
}
