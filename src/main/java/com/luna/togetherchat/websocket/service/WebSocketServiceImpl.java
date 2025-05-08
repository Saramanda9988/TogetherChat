package com.luna.togetherchat.websocket.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.luna.togetherchat.chat.domain.request.message.ChatMessageRequest;
import com.luna.togetherchat.chat.service.ChatService;
import com.luna.togetherchat.common.config.ThreadPoolConfig;
import com.luna.togetherchat.common.domain.dto.RequestInfo;
import com.luna.togetherchat.common.utils.RequestHolder;
import com.luna.togetherchat.websocket.domain.dto.WSChannelExtraDTO;
import com.luna.togetherchat.websocket.domain.vo.request.WSBaseResp;
import com.luna.togetherchat.websocket.util.NettyUtil;
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
}
