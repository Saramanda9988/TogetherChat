package com.luna.roomserver.room.event;

import com.luna.common.constant.MQConstant;
import com.luna.common.domain.dto.PushMessageDTO;
import com.luna.common.domain.vo.response.WSBaseResp;
import com.luna.common.utils.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PushService {
    @Autowired
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
