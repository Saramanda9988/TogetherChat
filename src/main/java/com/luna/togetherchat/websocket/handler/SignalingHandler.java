package com.luna.togetherchat.websocket.handler;

import com.luna.togetherchat.websocket.domain.vo.request.WSCallSignalingAction;
import io.netty.channel.Channel;

/**
 * WebRTC信令处理器接口
 */
public interface SignalingHandler {
    /**
     * 处理WebRTC信令
     *
     * @param signalingAction 信令动作
     * @param channel         WebSocket通道
     * @param callerId
     */
    void handleSignaling(WSCallSignalingAction signalingAction, Channel channel, Long callerId);
}