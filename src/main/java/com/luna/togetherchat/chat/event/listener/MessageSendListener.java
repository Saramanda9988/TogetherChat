package com.luna.togetherchat.chat.event.listener;


import com.luna.togetherchat.chat.dao.MessageDao;
import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.chat.event.MessageSendEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class MessageSendListener {

    @Autowired
    private MessageDao messageDao;;

    // 发送事件
    @TransactionalEventListener(
            phase = TransactionPhase.BEFORE_COMMIT,
            classes = MessageSendEvent.class,
            fallbackExecution = true
    )
    public void  messageRoute(MessageSendEvent event) {
        Message message = event.getMessage();
        messageDao.saveOrUpdate(message);
    }
}
