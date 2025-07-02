package com.luna.togetherchat.websocket.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.luna.togetherchat.call.domain.entity.Participant;
import com.luna.togetherchat.call.domain.entity.Session;
import com.luna.togetherchat.chat.domain.request.message.ChatMessageRequest;
import com.luna.togetherchat.chat.service.ChatService;
import com.luna.togetherchat.common.config.ThreadPoolConfig;
import com.luna.togetherchat.common.domain.dto.RequestInfo;
import com.luna.togetherchat.common.utils.RequestHolder;
import com.luna.togetherchat.websocket.domain.dto.WSChannelExtraDTO;
import com.luna.togetherchat.websocket.domain.vo.WSBaseResp;
import com.luna.togetherchat.websocket.domain.vo.signalling.*;
import com.luna.togetherchat.websocket.enums.WSReqTypeEnum;
import com.luna.togetherchat.websocket.util.NettyUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.*;
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

    /** 所有在线的用户和对应的通话会话 userId -> sessionId*/
    private static final ConcurrentHashMap<Long, Long> ONLINE_SESSION_UID_MAP = new ConcurrentHashMap<>();

    /** 在某个会议中的用户列表 sessionId -> userId列表*/
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Long>> ONLINE_SESSION_MAP = new ConcurrentHashMap<>();

    @Autowired
    private ChatService chatService;

    @Autowired
    @Qualifier(ThreadPoolConfig.WS_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /*==========================websocket连接==========================*/
    // 处理所有ws连接的事件

    @Override
    public List<Long> getOnlineUserId() {
        return ONLINE_UID_MAP.keySet().stream().toList();
    }

    public WSChannelExtraDTO getChannelExt(Channel channel) {
        return ONLINE_WS_MAP.get(channel);
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
        sendToUid(wsBaseResp, List.of(uid));
    }

    private void sendToUid(WSBaseResp<?> wsBaseResp, List<Long> uids) {
        for (Long uid : uids) {
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
            if (CollectionUtil.isEmpty(channels)) {
                log.info("用户：{}不在线", uid);
                return;
            }
            channels.forEach(channel -> {
                threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
            });
        }
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
        log.info("[HEARTBEAT]收到心跳消息 from 用户：{}", ONLINE_WS_MAP.get(channel).getUid());
        WSBaseResp<String> resp = new WSBaseResp<>();
        resp.setType(WSReqTypeEnum.HEARTBEAT.getType());
        resp.setData("pong");
        sendMsg(channel, resp);
    }

    /*=====================websocket音视频信令发送============================== */
    @Override
    public void handleEntry(WSEntry data, Channel channel) {
        if (ONLINE_SESSION_MAP.get(data.getMeetId()) == null) {
            log.info("会议不存在：{}", data.getMeetId());
            return;
        }
        CopyOnWriteArrayList<Long> userList = ONLINE_SESSION_MAP.get(data.getMeetId());
        userList.add(data.getUserId());
        ONLINE_SESSION_UID_MAP.putIfAbsent(data.getUserId(), data.getMeetId());

        WSBaseResp<WSEntry> resp = new WSBaseResp<>();
        resp.setType(data.getType());
        resp.setData(data);

        // 创建新列表，排除当前用户
        List<Long> targetUsers = userList.stream()
                .filter(uid -> !Objects.equals(uid, data.getUserId()))
                .toList();
        sendToUid(resp, targetUsers);
    }

    @Override
    public void handleOffer(WSOffer data, Channel channel) {
        Long targetId = data.getTargetId();
        if (ONLINE_UID_MAP.get(targetId) == null) {
            log.info("[OFFER]用户：{}离线", targetId);
            return;
        }
        data.setSourceId(RequestHolder.get().getUserId());
        WSBaseResp<WSOffer> resp = new WSBaseResp<>();
        resp.setType(WSReqTypeEnum.OFFER.getType());
        resp.setData(data);
        // 发送offer给目标用户
        sendToUid(resp, targetId);
    }

    @Override
    public void handleAnswer(WSAnswer data, Channel channel) {
        Long targetId = data.getTargetId();
        if (ONLINE_UID_MAP.get(targetId) == null) {
            log.info("[ANSWER]用户：{}离线", targetId);
            return;
        }
        data.setSourceId(RequestHolder.get().getUserId());
        WSBaseResp<WSAnswer> resp = new WSBaseResp<>();
        resp.setType(WSReqTypeEnum.ANSWER.getType());
        resp.setData(data);
        // 发送answer给目标用户
        sendToUid(resp, targetId);
    }

    @Override
    public void handleCandidate(WSCandidate data, Channel channel) {
        Long targetId = data.getTargetId();
        if (ONLINE_UID_MAP.get(targetId) == null) {
            log.info("[CANDIDATE]用户：{}离线", targetId);
            return;
        }
        data.setSourceId(RequestHolder.get().getUserId());
        WSBaseResp<WSCandidate> resp = new WSBaseResp<>();
        resp.setType(WSReqTypeEnum.CANDIDATE.getType());
        resp.setData(data);
        // 发送candidate给目标用户
        sendToUid(resp, targetId);
    }

    @Override
    public void handleLeave(WSLeave data, Channel channel) {
        if (ONLINE_SESSION_MAP.get(data.getMeetId()) == null) {
            log.info("[LEAVE]会议不存在：{}", data.getMeetId());
            return;
        }
        CopyOnWriteArrayList<Long> userList = ONLINE_SESSION_MAP.get(data.getMeetId());
        userList.remove(data.getSourceId());
        ONLINE_SESSION_UID_MAP.remove(data.getSourceId());
    }

    @Override
    public void handleReject(WSReject data, Channel channel) {
        if (ONLINE_SESSION_MAP.get(data.getMeetId()) == null) {
            log.info("[REJECT]会议不存在：{}", data.getMeetId());
            return;
        }
        data.setSourceId(ONLINE_WS_MAP.get(channel).getUid());
        WSBaseResp<WSReject> resp = new WSBaseResp<>();
        resp.setType(WSReqTypeEnum.REJECT.getType());
        resp.setData(data);
        // 发送拒绝消息给目标用户
        sendToUid(resp, data.getTargetId());
    }

    @Override
    public void sendJoinSignalling(Session session, List<Participant> participants) {
        Long userId = RequestHolder.get().getUserId();
        ONLINE_SESSION_UID_MAP.put(userId, session.getSessionId());
        CopyOnWriteArrayList<Long> userList = new CopyOnWriteArrayList<>();
        userList.add(userId);
        ONLINE_SESSION_MAP.put(session.getSessionId(), userList);
        for (Long targetId : userList) {
            WSJoin wsJoin = new WSJoin();
            wsJoin.setMeetId(session.getSessionId());
            wsJoin.setType(WSReqTypeEnum.JOIN.getType());
            wsJoin.setSourceId(userId);
            wsJoin.setTargetId(targetId);
            wsJoin.setKey(UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 16));
            WSBaseResp<WSJoin> resp = new WSBaseResp<>();
            resp.setType(WSReqTypeEnum.JOIN.getType());
            resp.setData(wsJoin);
            sendToUid(resp, targetId);
        }
    }

    public void sendCancelSignalling(Session session) {
        WSCancel wsCancel = new WSCancel();
        wsCancel.setMeetId(session.getSessionId());
        wsCancel.setSourceId(RequestHolder.get().getUserId());
        wsCancel.setType(WSReqTypeEnum.CANCEL.getType());
        WSBaseResp<WSCancel> resp = new WSBaseResp<>();
        resp.setType(WSReqTypeEnum.CANCEL.getType());
        resp.setData(wsCancel);

        // 发送信令给所有参与者
        CopyOnWriteArrayList<Long> userList = ONLINE_SESSION_MAP.get(session.getSessionId());
        if (CollectionUtil.isNotEmpty(userList)) {
            sendToUid(resp, userList);
        }
    }
}
