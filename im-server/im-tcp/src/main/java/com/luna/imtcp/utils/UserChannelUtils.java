package com.luna.imtcp.utils;

import com.luna.common.domain.dto.RequestInfo;
import com.luna.common.utils.JsonUtils;
import com.luna.common.utils.RedisUtils;
import com.luna.imtcp.api.constants.ChannelConstants;
import com.luna.imtcp.api.constants.WebConstants;
import com.luna.imtcp.api.enums.ConnectStateEnums;
import com.luna.imtcp.api.user.UserSession;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UserChannelUtils {

    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final Map<RequestInfo, Channel> USER_CHANNEL = new ConcurrentHashMap<>();
    private static final Object bindLocker = new Object();
    private static final Object removeLocker = new Object();

    public static void bind(RequestInfo requestInfo, Channel channel) {
        synchronized (bindLocker) {
            // 此时channel一定已经在ChannelGroup中了

            // 之前已经绑定过了，移除并释放掉之前绑定的channel
            // LoginStatusMap  userChannelKey --> channel
            if (USER_CHANNEL.containsKey(requestInfo)) {
                Channel oldChannel = USER_CHANNEL.get(requestInfo);
                CHANNEL_GROUP.remove(oldChannel);
                oldChannel.close();
            }

            // 双向绑定
            // channel -> user property
            channel.attr(AttributeKey.valueOf(ChannelConstants.UserId)).set(requestInfo.getUserId());
            channel.attr(AttributeKey.valueOf(ChannelConstants.AppId)).set(requestInfo.getAppId());
            channel.attr(AttributeKey.valueOf(ChannelConstants.ClientType)).set(requestInfo.getClientType());
            channel.attr(AttributeKey.valueOf(ChannelConstants.imei)).set(requestInfo.getImei());
            channel.attr(AttributeKey.valueOf(ChannelConstants.ClientImei)).set(requestInfo.getClientType() + ":" + requestInfo.getImei());

            // userChannelKey -> channel
            USER_CHANNEL.put(requestInfo, channel);
        }
    }

    /**
     * 从通道中获取用户信息。只要 userClientDto 和 channel 绑定中，这个方法就一定能获取的到
     * @param channel
     * @return
     */
    public static RequestInfo getUserInfo(Channel channel) {
        Long userId = Long.parseLong(channel.attr(AttributeKey.valueOf(ChannelConstants.UserId)).get().toString());
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(ChannelConstants.AppId)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(ChannelConstants.ClientType)).get();
        String imei = (String) channel.attr(AttributeKey.valueOf(ChannelConstants.imei)).get();

        return RequestInfo
                .builder()
                .userId(userId)
                .imei(imei)
                .appId(appId)
                .clientType(clientType)
                .build();
    }

    public static void add(Channel channel) {
        CHANNEL_GROUP.add(channel);
    }

    /**
     * 双删，redisson 和 本地缓存
     * @param channel
     */
    public static void remove(Channel channel) {
        synchronized(removeLocker) { // 确保原子性

            RequestInfo userInfo = getUserInfo(channel);

            // userInfo 有可能为空。可能 chanelActive 之后，由于前端原因（或者网络原因）没有及时绑定 userInfo。
            // 此时 netty 认为 channelInactive 了，就移除通道，这时 userInfo 就是 null
            if (ObjectUtils.isEmpty(userInfo)) {
                log.info("用户信息不存在，请检查");
                return ;
            }
            // TODO 延迟双删：等待数据包传输完再删除 channel
            USER_CHANNEL.remove(userInfo);
            CHANNEL_GROUP.remove(channel);

            // Redis 删除用户 Session
            removeSession(userInfo);

            // 关闭channel
            channel.close();
        }
    }

    public static void remove(RequestInfo userClientDto) {
        // 确保原子性
        synchronized(removeLocker) {

            Channel channel = USER_CHANNEL.get(userClientDto);
            USER_CHANNEL.remove(userClientDto);
            CHANNEL_GROUP.remove(channel);

            // 关闭channel
            if (!ObjectUtils.isEmpty(channel)) {
                channel.close();
            }
        }
    }

    private static void removeSession(RequestInfo userInfo) {
        String key = userInfo.getAppId() + WebConstants.UserSessionConstants + userInfo.getUserId();
        String field = userInfo.getClientType() + ":" + userInfo.getImei();
        RedisUtils.hdel(key, field);
    }

    /**
     * 判断用户是否在线
     * LoginStatusMap 和 channelGroup 中均能找得到对应的 channel 说明用户在线
     * @return      在线就返回对应的channel，不在线返回null
     */
    public static Channel isBind(RequestInfo userClientDto) {
        Channel channel = USER_CHANNEL.get(userClientDto);
        if (ObjectUtils.isEmpty(channel)) {
            return null;
        }
        return CHANNEL_GROUP.find(channel.id());
    }

    public static boolean isBind(Channel channel) {
        RequestInfo userInfo = getUserInfo(channel);
        return !ObjectUtils.isEmpty(userInfo) &&
                !ObjectUtils.isEmpty(USER_CHANNEL.get(userInfo));
    }

    public static void forceOffLine(RequestInfo requestInfo) {
        Channel channel = isBind(requestInfo);
        if (ObjectUtils.isEmpty(channel)) {
            String mapKey = requestInfo.getAppId() + WebConstants.UserSessionConstants + requestInfo.getUserId();
            String fieldKey = requestInfo.getClientType() + ":" + requestInfo.getImei();
            String userSessionValue = (String) RedisUtils.hget(mapKey, fieldKey);

            if (!StringUtils.isBlank(userSessionValue)) {
                UserSession userSession = JsonUtils.toObj(userSessionValue, UserSession.class);
                userSession.setConnectState(ConnectStateEnums.DISCONNECTED.getCode()); // 离线状态码
                RedisUtils.hset(mapKey, fieldKey, JsonUtils.toStr(userSession));
            }
            // 移除通道。服务端单方面关闭连接。前端心跳会发送失败
            remove(requestInfo);
        }
    }

    public static void forceOffLine(Channel channel) {
        RequestInfo userInfo = getUserInfo(channel);
        try {
            forceOffLine(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 遍历某用户绑定的所有 Channel
     * @param appId
     * @param userId
     * @return
     */
    public static List<Channel> getUserChannels(Integer appId, String userId) {
        Set<RequestInfo> channelInfos = USER_CHANNEL.keySet();
        List<Channel> channels = new ArrayList<>();

        channelInfos.forEach(channel -> {
            if (appId.equals(channel.getAppId()) && userId.equals(channel.getUserId().toString())) {
                channels.add(USER_CHANNEL.get(channel));
            }
        });
        return channels;
    }

    public static Channel getUserChannel(Integer appId, Long userId, Integer clientType, String imei) {
        RequestInfo dto = new RequestInfo();
        dto.setUserId(userId);
        dto.setAppId(appId);
        dto.setClientType(clientType);
        dto.setImei(imei);
        if (!USER_CHANNEL.containsKey(dto)) {
            log.error("channel 通道 没有 [{}] 信息", dto);
            return null;
        }
        return USER_CHANNEL.get(dto);
    }

    public synchronized static void print() {
        log.info("所有通道的长id：");
        for (Channel channel : CHANNEL_GROUP) {
            log.info(channel.id().asLongText());
        }
        log.info("userId -> channel 的映射：");
        for (Map.Entry<RequestInfo, Channel> entry : USER_CHANNEL.entrySet()) {
            log.info("userId: {} ---> channelId: {}", entry.getKey(), entry.getValue().id().asLongText());
        }
    }
}
