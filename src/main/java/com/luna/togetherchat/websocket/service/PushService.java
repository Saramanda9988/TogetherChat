package com.luna.togetherchat.websocket.service;


import com.luna.togetherchat.chat.event.MQProducer;
import com.luna.togetherchat.common.constant.MQConstant;
import com.luna.togetherchat.websocket.domain.dto.PushMessageDTO;
import com.luna.togetherchat.websocket.domain.vo.request.WSBaseResp;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-12
 */
@Service
public class PushService {

    @Resource
    private MQProducer mqProducer;

    public void sendPushMsg(WSBaseResp<?> msg, List<Long> uidList) {
        mqProducer.sendMsg(MQConstant.PUSH_EXCHANGE, new PushMessageDTO(uidList, msg));
    }

    public void sendPushMsg(WSBaseResp<?> msg, Long uid) {
        mqProducer.sendMsg(MQConstant.PUSH_EXCHANGE, new PushMessageDTO(uid, msg));
    }

    public void sendPushMsg(WSBaseResp<?> msg) {
        mqProducer.sendMsg(MQConstant.PUSH_EXCHANGE, new PushMessageDTO(msg));
    }
}
