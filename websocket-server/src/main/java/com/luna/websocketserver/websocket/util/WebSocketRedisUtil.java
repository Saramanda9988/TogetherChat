package com.luna.websocketserver.websocket.util;

import com.luna.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

/**
 * WebSocket Redis 工具类，用于存储用户在线状态和服务地址映射
 */
@Slf4j
@Component
public class WebSocketRedisUtil {

    private static final String REDIS_USER_SERVER_KEY = "websocket:online:user:";
    private static final String REDIS_SERVER_USER_KEY = "websocket:online:server:";

    @Value("${websocket.server.port}")
    private String webSocketPort;

    private String serverAddress;

    /**
     * 获取当前服务器地址
     * @return 服务器地址，格式为 ip:port
     */
    public String getServerAddress() {
        if (serverAddress == null) {
            try {
                String hostAddress = InetAddress.getLocalHost().getHostAddress();
                serverAddress = hostAddress + ":" + webSocketPort;
            } catch (UnknownHostException e) {
                log.error("获取本机IP地址失败", e);
                serverAddress = "localhost:" + webSocketPort;
            }
        }
        return serverAddress;
    }

    /**
     * 用户上线时，在Redis中存储映射关系
     * @param uid 用户ID
     */
    public void userOnline(Long uid) {
        if (uid == null) {
            return;
        }
        
        String serverAddr = getServerAddress();
        
        // 存储 uid -> 服务地址
        RedisUtils.set(REDIS_USER_SERVER_KEY + uid, serverAddr);
        
        // 存储 服务地址 -> uid 集合
        RedisUtils.sSet(REDIS_SERVER_USER_KEY + serverAddr, uid); // 设置过期时间为24小时
        
        log.info("用户 {} 上线，服务地址：{}", uid, serverAddr);
    }

    /**
     * 用户下线时，从Redis中删除映射关系
     * @param uid 用户ID
     */
    public void userOffline(Long uid) {
        if (uid == null) {
            return;
        }
        
        String serverAddr = getServerAddress();
        
        // 获取用户当前的服务地址
        String userServerAddr = RedisUtils.getStr(REDIS_USER_SERVER_KEY + uid);
        
        // 删除 uid -> 服务地址
        RedisUtils.del(REDIS_USER_SERVER_KEY + uid);
        
        // 从服务地址对应的用户集合中移除该用户
        if (userServerAddr != null) {
            RedisUtils.setRemove(REDIS_SERVER_USER_KEY + userServerAddr, uid);
        }
        
        log.info("用户 {} 下线，服务地址：{}", uid, serverAddr);
    }

    /**
     * 获取用户当前所在的服务地址
     * @param uid 用户ID
     * @return 服务地址
     */
    public String getUserServerAddress(Long uid) {
        return RedisUtils.getStr(REDIS_USER_SERVER_KEY + uid);
    }

    /**
     * 获取指定服务器上的所有在线用户ID
     * @param serverAddr 服务器地址
     * @return 用户ID集合
     */
    public Set<String> getServerOnlineUsers(String serverAddr) {
        return RedisUtils.sGet(REDIS_SERVER_USER_KEY + serverAddr);
    }
} 