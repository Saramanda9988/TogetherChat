package com.luna.togetherchat.chat.service.strategy.flink.sink;

import com.luna.togetherchat.room.domain.entity.RoomMember;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RoomMemberSink extends AbstractSink<RoomMember>{

    @Value("${tuanchat.room_member.table.name}")
    private String room_member;

    @Override
    public String getTableName() {
        return room_member;
    }

}
