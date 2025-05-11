package com.luna.togetherchat.websocket.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.luna.togetherchat.call.domain.request.CallingRequest;
import com.luna.togetherchat.chat.domain.request.message.ChatMessageRequest;
import com.luna.togetherchat.chat.service.ChatService;
import com.luna.togetherchat.common.config.ThreadPoolConfig;
import com.luna.togetherchat.common.domain.dto.RequestInfo;
import com.luna.togetherchat.common.utils.RequestHolder;
import com.luna.togetherchat.websocket.domain.dto.WSChannelExtraDTO;
import com.luna.togetherchat.websocket.domain.enums.WSRespTypeEnum;
import com.luna.togetherchat.websocket.domain.vo.request.*;
import com.luna.togetherchat.websocket.util.NettyUtil;
import com.luna.togetherchat.call.enums.CallingSignalEnum;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.luna.togetherchat.call.enums.CallingSignalEnum.OFFER;

/**
 * Description: websocket处理类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19 16:21
 */
@Component
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    /** 所有已连接的websocket连接列表和一些额外参数 Channel -> userId */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /** 所有在线的用户和对应的socket userId -> Channel列表 */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();

    @Autowired
    private ChatService chatService;

    @Autowired
    @Qualifier(ThreadPoolConfig.WS_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /*==========================websocket连接==========================*/
    // 处理所有ws连接的事件

    public List<Long> getOnlineUserId() {
        return ONLINE_UID_MAP.keySet().stream().toList();
    }

    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    // 如果第一次连接，携带了token，就启用上线模块
    @Override
    public void authorize(Channel channel, String token) {
        if (Objects.isNull(token)) { return; }
        Long uid = Long.parseLong(token);
        online(channel, uid);
    }

    // 用户上线，更新onlineUidMap 如果ONLINE_WS_MAP 在线列表不存在，也更新
    private void online(Channel channel, Long uid) {
        getOrInitChannelExt(channel).setUid(uid);
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        ONLINE_UID_MAP.get(uid).add(channel);
        NettyUtil.setAttr(channel, NettyUtil.UID, uid);
    }

    // 如果在线列表不存在，就先把该channel放进在线列表
    private WSChannelExtraDTO getOrInitChannelExt(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.getOrDefault(channel, new WSChannelExtraDTO());
        WSChannelExtraDTO old = ONLINE_WS_MAP.putIfAbsent(channel, wsChannelExtraDTO);
        return ObjectUtil.isNull(old) ? wsChannelExtraDTO : old;
    }

    /*====================websocket断开链接==========================*/
    // 用户下线
    private boolean offline(Channel channel, Optional<Long> uidOptional) {
        ONLINE_WS_MAP.remove(channel);
        if (uidOptional.isPresent()) {
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uidOptional.get());
            if (CollectionUtil.isNotEmpty(channels)) {
                channels.removeIf(ch -> Objects.equals(ch, channel));
            }
            return CollectionUtil.isEmpty(ONLINE_UID_MAP.get(uidOptional.get()));
        }
        return true;
    }

    public void removed(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        Optional<Long> uidOptional = Optional.ofNullable(wsChannelExtraDTO)
                .map(WSChannelExtraDTO::getUid);
        boolean offlineAll = offline(channel, uidOptional);
    }

    /*=====================websocket消息发送============================== */
    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid) {
        // entrySet的值不是快照数据,但是它支持遍历，所以无所谓了，不用快照也行。
        ONLINE_WS_MAP.forEach((channel, ext) -> {
            if (Objects.nonNull(skipUid) && Objects.equals(ext.getUid(), skipUid)) { return; }
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp) {
        sendToAllOnline(wsBaseResp, null);
    }

    @Override
    public void sendToUid(WSBaseResp<?> wsBaseResp, Long uid) {
        CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
        if (CollectionUtil.isEmpty(channels)) {
            log.info("用户：{}不在线", uid);
            return;
        }
        channels.forEach(channel -> {
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    // 给本地channel发送消息
    private void sendMsg(Channel channel, WSBaseResp<?> wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }

    @Override
    public void sendMessage(ChatMessageRequest request, Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        RequestInfo info = new RequestInfo();
        info.setUserId(wsChannelExtraDTO.getUid());
        RequestHolder.set(info);

        chatService.sendMessage(request, wsChannelExtraDTO.getUid());
    }

    @Override
        public void handleHeartBeat(Channel channel) {
        log.info("收到心跳消息 from 用户：{}", ONLINE_WS_MAP.get(channel).getUid());
    }

    /*=====================websocket音视频信令发送============================== */
    @Override
    public void sendWebrtcSignal(String content, Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        RequestInfo info = new RequestInfo();
        info.setUserId(wsChannelExtraDTO.getUid());
        RequestHolder.set(info);
    }

    /**
     * 处理前端信令
     * @param callingRequest
     * @param channel
     */
    @Override
    public void handleCallSignaling(CallingRequest callingRequest, Channel channel) {
        // 获取发送者信息
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        Long callerId = wsChannelExtraDTO.getUid();
        
        // 设置请求上下文
        RequestInfo info = new RequestInfo();
        info.setUserId(callerId);
        RequestHolder.set(info);
        
        CallingSignalEnum callingSignalEnum = CallingSignalEnum.of(callingRequest.getType());
        log.info("收到WebRTC信令: type={}, caller={}, receivers={}", 
                 callingSignalEnum, callerId, callingRequest.getReceiverId());
        
        switch (callingSignalEnum) {
            case OFFER:
                // 处理通话请求信令
                handleOfferSignal(callingRequest, callerId);
                break;
            case ACCEPT:
                // 处理接受通话信令
                handleAcceptSignal(callingRequest, callerId);
                break;
            case REJECT:
                // 处理拒绝通话信令
                handleRejectSignal(callingRequest, callerId);
                break;
            case CANCEL:
                // 处理取消通话信令
                handleCancelSignal(callingRequest, callerId);
                break;
            default:
                log.error("未知信令类型: {}", callingRequest.getType());
        }
    }

    /**
     * 处理通话请求信令(OFFER)
     */
    private void handleOfferSignal(CallingRequest callingRequest, Long callerId) {
        // 构建通话请求消息
        WSCallRequest callRequest = WSCallRequest.builder()
                .callerId(callerId)
                .receiverId(callingRequest.getReceiverId().get(0)) // 假设只有一个接收者
                .callType(callingRequest.getExtra()) // 假设extra字段存储通话类型(1:语音,2:视频)
                .expireTime(callingRequest.getExpireTime())
                .build();
        
        // 构建WebSocket响应
        WSBaseResp<WSCallRequest> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.CALL_SIGNAL.getType());
        wsBaseResp.setData(callRequest);
        
        // 发送给接收者
        callingRequest.getReceiverId().forEach(receiverId -> {
            sendToUid(wsBaseResp, receiverId);
            log.info("向用户{}发送通话请求", receiverId);
        });
    }

    /**
     * 处理接受通话信令(ACCEPT)
     */
    private void handleAcceptSignal(CallingRequest callingRequest, Long accepterId) {
        // 构建接受通话消息
        WSCallAgree callAgree = WSCallAgree.builder()
                .callerId(callingRequest.getReceiverId().get(0)) // 原始呼叫者
                .receiverId(accepterId) // 接受通话的人
                .callType(callingRequest.getExtra())
                .build();
        
        // 构建WebSocket响应
        WSBaseResp<WSCallAgree> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.AGREE_CALL.getType());
        wsBaseResp.setData(callAgree);
        
        // 发送给原始呼叫者
        Long originalCallerId = callingRequest.getReceiverId().get(0);
        sendToUid(wsBaseResp, originalCallerId);
        log.info("用户{}接受了来自用户{}的通话", accepterId, originalCallerId);
    }

    /**
     * 处理拒绝通话信令(REJECT)
     */
    private void handleRejectSignal(CallingRequest callingRequest, Long rejecterId) {
        // 构建拒绝通话消息
        WSCallReject callReject = WSCallReject.builder()
                .callerId(callingRequest.getReceiverId().get(0)) // 原始呼叫者
                .receiverId(rejecterId) // 拒绝通话的人
                .rejectReason(callingRequest.getExtra()) // 假设extra字段存储拒绝原因
                .build();
        
        // 构建WebSocket响应
        WSBaseResp<WSCallReject> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.REJECT_CALL.getType());
        wsBaseResp.setData(callReject);
        
        // 发送给原始呼叫者
        Long originalCallerId = callingRequest.getReceiverId().get(0);
        sendToUid(wsBaseResp, originalCallerId);
        log.info("用户{}拒绝了来自用户{}的通话", rejecterId, originalCallerId);
    }

    /**
     * 处理取消通话信令(CANCEL)
     */
    private void handleCancelSignal(CallingRequest callingRequest, Long cancelerId) {
        // 构建取消通话消息
        WSCallCancel callCancel = WSCallCancel.builder()
                .userId(cancelerId)
                .cancelReason(callingRequest.getExtra()) // 假设extra字段存储取消原因
                .build();
        
        // 构建WebSocket响应
        WSBaseResp<WSCallCancel> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.CANCEL_CALL.getType());
        wsBaseResp.setData(callCancel);
        
        // 发送给所有接收者
        callingRequest.getReceiverId().forEach(receiverId -> {
            sendToUid(wsBaseResp, receiverId);
            log.info("向用户{}发送通话取消通知", receiverId);
        });
    }

}
