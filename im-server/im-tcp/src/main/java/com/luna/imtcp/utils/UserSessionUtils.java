package com.luna.imtcp.utils;

import com.luna.common.utils.JsonUtils;
import com.luna.common.utils.RedisUtils;
import com.luna.imtcp.api.constants.WebConstants;
import com.luna.imtcp.api.user.UserSession;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * UserSession工具类
 * 用于从Redis中获取用户会话信息
 */
@Slf4j
public class UserSessionUtils {

    /**
     * 获取单个用户的所有在线会话
     * 
     * @param appId 应用ID
     * @param userId 用户ID
     * @return 用户的所有在线会话列表
     */
    public static List<UserSession> getUserSessions(Integer appId, Long userId) {
        List<UserSession> sessions = new ArrayList<>();
        
        try {
            // Redis Key: appId:userSession:userId
            String mapKey = appId + WebConstants.UserSessionConstants + userId;
            
            // 获取用户的所有会话信息
            Map<String, String> sessionMap = RedisUtils.hgetAll(mapKey);
            
            if (sessionMap == null || sessionMap.isEmpty()) {
                log.debug("用户没有在线会话: appId={}, userId={}", appId, userId);
                return sessions;
            }
            
            // 解析每个会话信息
            for (Map.Entry<String, String> entry : sessionMap.entrySet()) {
                try {
                    UserSession session = JsonUtils.toObj(entry.getValue(), UserSession.class);
                    if (session != null && session.getConnectState() == 1) { // 只返回在线状态的会话
                        sessions.add(session);
                    }
                } catch (Exception e) {
                    log.warn("解析用户会话失败: fieldKey={}, value={}", entry.getKey(), entry.getValue(), e);
                }
            }
            
            log.debug("获取到用户在线会话: appId={}, userId={}, sessionCount={}", appId, userId, sessions.size());
            
        } catch (Exception e) {
            log.error("获取用户会话失败: appId={}, userId={}", appId, userId, e);
        }
        
        return sessions;
    }

    /**
     * 获取多个用户的所有在线会话
     * 
     * @param appId 应用ID
     * @param userIds 用户ID列表
     * @return Map<userId, List<UserSession>>
     */
    public static Map<Long, List<UserSession>> getUsersSessions(Integer appId, List<Long> userIds) {
        Map<Long, List<UserSession>> resultMap = new HashMap<>();
        
        if (userIds == null || userIds.isEmpty()) {
            return resultMap;
        }
        
        for (Long userId : userIds) {
            List<UserSession> sessions = getUserSessions(appId, userId);
            if (!sessions.isEmpty()) {
                resultMap.put(userId, sessions);
            }
        }
        
        log.debug("获取到多个用户的在线会话: appId={}, totalUsers={}, onlineUsers={}", 
                appId, userIds.size(), resultMap.size());
        
        return resultMap;
    }

    /**
     * 获取指定用户的特定客户端会话
     * 
     * @param appId 应用ID
     * @param userId 用户ID
     * @param clientType 客户端类型
     * @param imei 设备标识
     * @return 用户会话，如果不存在或不在线则返回null
     */
    public static UserSession getUserSession(Integer appId, Long userId, Integer clientType, String imei) {
        try {
            // Redis Key: appId:userSession:userId
            String mapKey = appId + WebConstants.UserSessionConstants + userId;
            // Field Key: clientType:imei
            String fieldKey = clientType + ":" + imei;
            
            String sessionJson = (String) RedisUtils.hget(mapKey, fieldKey);
            
            if (sessionJson == null) {
                log.debug("用户指定客户端会话不存在: appId={}, userId={}, clientType={}, imei={}", 
                        appId, userId, clientType, imei);
                return null;
            }
            
            UserSession session = JsonUtils.toObj(sessionJson, UserSession.class);
            
            // 检查是否在线
            if (session != null && session.getConnectState() == 1) {
                return session;
            } else {
                log.debug("用户指定客户端不在线: appId={}, userId={}, clientType={}, imei={}", 
                        appId, userId, clientType, imei);
                return null;
            }
            
        } catch (Exception e) {
            log.error("获取用户指定客户端会话失败: appId={}, userId={}, clientType={}, imei={}", 
                    appId, userId, clientType, imei, e);
            return null;
        }
    }

    /**
     * 检查用户是否在线
     * 
     * @param appId 应用ID
     * @param userId 用户ID
     * @return 是否在线
     */
    public static boolean isUserOnline(Integer appId, Long userId) {
        List<UserSession> sessions = getUserSessions(appId, userId);
        return !sessions.isEmpty();
    }

    /**
     * 检查用户指定客户端是否在线
     * 
     * @param appId 应用ID
     * @param userId 用户ID
     * @param clientType 客户端类型
     * @param imei 设备标识
     * @return 是否在线
     */
    public static boolean isUserClientOnline(Integer appId, Long userId, Integer clientType, String imei) {
        UserSession session = getUserSession(appId, userId, clientType, imei);
        return session != null;
    }

    /**
     * 获取当前服务器ID
     * 这里需要和UserChannelUtils中的实现保持一致
     * 
     * @return 当前服务器ID
     */
    public static String getCurrentServerId() {
        try {
            String hostName = java.net.InetAddress.getLocalHost().getHostName();
            String hostAddress = java.net.InetAddress.getLocalHost().getHostAddress();
            return hostAddress + ":" + hostName;
        } catch (Exception e) {
            log.warn("获取服务器ID失败，使用默认值", e);
            return "default-server-" + System.currentTimeMillis();
        }
    }

    /**
     * 按服务器分组用户会话
     * 
     * @param sessions 用户会话列表
     * @return Map<serverId, List<UserSession>>
     */
    public static Map<String, List<UserSession>> groupSessionsByServer(List<UserSession> sessions) {
        Map<String, List<UserSession>> serverGroupMap = new HashMap<>();
        
        for (UserSession session : sessions) {
            String serverId = getServerIdFromSession(session);
            serverGroupMap.computeIfAbsent(serverId, k -> new ArrayList<>()).add(session);
        }
        
        return serverGroupMap;
    }

    /**
     * 从UserSession中获取服务器ID
     * 优先使用brokerHost:brokerPort，如果不存在则使用brokerId
     * 
     * @param session 用户会话
     * @return 服务器ID
     */
    private static String getServerIdFromSession(UserSession session) {
        if (session.getBrokerIp() != null && session.getBrokerPort() != null) {
            return session.getBrokerIp() + ":" + session.getBrokerPort();
        } else if (session.getBrokerId() != null) {
            return "broker-" + session.getBrokerId();
        } else {
            return "unknown-server";
        }
    }
}