//package com.luna.chatserver.chat.service.strategy.flink.sink;
//
//import com.luna.chatserver.room.domain.entity.RoomMember;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RoomMemberSink extends AbstractSink<RoomMember>{
//
//    @Value("${chatserver.room_member.table.name}")
//    private String room_member;
//
//    @Override
//    public String getTableName() {
//        return room_member;
//    }
//
//}
