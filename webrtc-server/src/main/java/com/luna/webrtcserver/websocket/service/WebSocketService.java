package com.luna.webrtcserver.websocket.service;


import com.luna.webrtcserver.call.domain.entity.Participant;
import com.luna.webrtcserver.call.domain.entity.Session;
import com.luna.webrtcserver.chat.domain.request.message.ChatMessageRequest;
import com.luna.webrtcserver.websocket.domain.vo.WSBaseResp;
import com.luna.webrtcserver.websocket.domain.vo.signalling.*;
import io.netty.channel.Channel;

import java.util.List;

public interface WebSocketService {
    List<Long> getOnlineUserId();

    /**
     * 处理所有ws连接的事件
     *
     * @param channel
     */
    void connect(Channel channel);

    /**
     * 推动消息给所有在线的人
     *
     * @param wsBaseResp 发送的消息体
     * @param skipUid    需要跳过的人
     */
    void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid);

    /**
     * 推动消息给所有在线的人
     *
     * @param wsBaseResp 发送的消息体
     */
    void sendToAllOnline(WSBaseResp<?> wsBaseResp);

    void sendToUid(WSBaseResp<?> wsBaseResp, Long uid);

    void authorize(Channel channel, String token);

    void sendMessage(ChatMessageRequest bean, Channel channel);
    /**
     * 处理ws断开连接的事件
     *
     * @param channel
     */
    void removed(Channel channel);

    void handleHeartBeat(Channel channel);

    void handleEntry(WSEntry data, Channel channel);

    void handleOffer(WSOffer data, Channel channel);

    void handleAnswer(WSAnswer data, Channel channel);

    void handleCandidate(WSCandidate data, Channel channel);

    void handleLeave(WSLeave data, Channel channel);

    void handleReject(WSReject data, Channel channel);

    int getActiveConnectionCount();

    int getOnlineUserCount();

    void handleCancel(WSCancel data, Channel channel);

    void handleJoin(WSJoin data, Channel channel);

    void addSession(Session session, Long callerId);

    void endSession(Long sessionId, Long managerId);
}
