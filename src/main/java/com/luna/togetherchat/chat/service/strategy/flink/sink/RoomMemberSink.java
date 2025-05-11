package com.luna.togetherchat.chat.service.strategy.flink.sink;

import com.luna.togetherchat.group.domain.entity.GroupMember;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RoomMemberSink extends AbstractSink<GroupMember>{

    @Value("${togetherchat.room_member.table.name}")
    private String room_member;

    @Override
    public String getTableName() {
        return room_member;
    }

}
