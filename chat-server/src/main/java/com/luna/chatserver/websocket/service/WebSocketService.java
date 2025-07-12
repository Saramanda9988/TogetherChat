package com.luna.chatserver.websocket.service;


import com.luna.chatserver.chat.domain.request.message.ChatMessageRequest;
import com.luna.chatserver.websocket.domain.vo.WSBaseResp;
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

    int getActiveConnectionCount();

    int getOnlineUserCount();
}
