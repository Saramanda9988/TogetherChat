package com.luna.imtcp.service;

import com.luna.imtcp.api.service.ImPushService;
import com.luna.imtcp.api.user.UserSession;
import com.luna.imtcp.api.vo.msgBody.MessageVOBody;
import com.luna.imtcp.utils.MessagePushUtils;
import com.luna.imtcp.utils.UserChannelUtils;
import com.luna.imtcp.utils.UserSessionUtils;
import com.luna.messageserver.api.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.cluster.specifyaddress.Address;
import org.apache.dubbo.rpc.cluster.specifyaddress.UserSpecifiedAddressUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 分布式推送服务
 * 负责将消息推送给用户，支持本地推送和远程推送
 */
@Slf4j
@Service
public class PushService {

    @DubboReference(group = "im-push-service", check = false, lazy = true)
    private ImPushService imPushService;

    /**
     * 推送消息给单个用户的所有在线设备
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @param messageBody 消息体
     * @return 推送是否成功
     */
    public boolean pushMessageToUser(Integer appId, Long userId, MessageDTO messageBody) {
        try {
            log.info("开始推送消息给用户: appId={}, userId={}, messageId={}",
                    appId, userId, messageBody.getMessageId());

            // 1. 获取用户的所有在线会话
            List<UserSession> sessions = UserSessionUtils.getUserSessions(appId, userId);

            if (sessions.isEmpty()) {
                log.info("用户不在线，无法推送消息: appId={}, userId={}", appId, userId);
                return false;
            }

            // 2. 按服务器分组会话
            Map<String, List<UserSession>> serverGroupMap = UserSessionUtils.groupSessionsByServer(sessions);

            boolean allSuccess = true;
            String currentServerId = UserSessionUtils.getCurrentServerId();

            // 3. 遍历每个服务器的会话进行推送
            for (Map.Entry<String, List<UserSession>> entry : serverGroupMap.entrySet()) {
                String serverId = entry.getKey();
                List<UserSession> serverSessions = entry.getValue();

                if (currentServerId.equals(serverId)) {
                    // 本地推送
                    boolean localSuccess = pushToLocalSessions(appId, serverSessions, messageBody);
                    allSuccess = allSuccess && localSuccess;
                } else {
                    // 远程推送
                    boolean remoteSuccess = pushToRemoteSessions(appId, serverId, serverSessions, messageBody);
                    allSuccess = allSuccess && remoteSuccess;
                }
            }

            log.info("推送消息完成: appId={}, userId={}, messageId={}, success={}",
                    appId, userId, messageBody.getMessageId(), allSuccess);

            return allSuccess;

        } catch (Exception e) {
            log.error("推送消息给用户失败: appId={}, userId={}, messageId={}",
                    appId, userId, messageBody.getMessageId(), e);
            return false;
        }
    }

    /**
     * 推送消息给用户指定客户端
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @param clientType 客户端类型
     * @param imei 设备标识
     * @param messageBody 消息体
     * @return 推送是否成功
     */
    public boolean pushMessageToUserClient(Integer appId, Long userId, Integer clientType, String imei, MessageDTO messageBody) {
        try {
            log.info("开始推送消息给用户指定客户端: appId={}, userId={}, clientType={}, imei={}, messageId={}",
                    appId, userId, clientType, imei, messageBody.getMessageId());

            // 1. 获取用户指定客户端会话
            UserSession session = UserSessionUtils.getUserSession(appId, userId, clientType, imei);

            if (session == null) {
                log.info("用户指定客户端不在线: appId={}, userId={}, clientType={}, imei={}",
                        appId, userId, clientType, imei);
                return false;
            }

            // 2. 检查是否在当前服务器
            if (UserChannelUtils.getUserChannel(appId, userId, clientType, imei) != null) {
                // 本地推送
                return pushToLocalSession(appId, session, messageBody);
            } else {
                // 远程推送
                return pushToRemoteSession(appId, session, messageBody);
            }

        } catch (Exception e) {
            log.error("推送消息给用户指定客户端失败: appId={}, userId={}, clientType={}, imei={}, messageId={}",
                    appId, userId, clientType, imei, messageBody.getMessageId(), e);
            return false;
        }
    }

    /**
     * 批量推送消息给多个用户
     *
     * @param appId 应用ID
     * @param userIds 用户ID列表
     * @param messageBody 消息体
     * @return 推送结果统计
     */
    public PushResult pushMessageToUsers(Integer appId, List<Long> userIds, MessageDTO messageBody) {
        PushResult result = new PushResult();

        if (userIds == null || userIds.isEmpty()) {
            log.warn("用户ID列表为空，无法推送消息");
            return result;
        }

        log.info("开始批量推送消息: appId={}, userCount={}, messageId={}",
                appId, userIds.size(), messageBody.getMessageId());

        for (Long userId : userIds) {
            try {
                boolean success = pushMessageToUser(appId, userId, messageBody);
                if (success) {
                    result.addSuccess(userId);
                } else {
                    result.addFailed(userId);
                }
            } catch (Exception e) {
                log.error("推送消息给用户失败: userId={}", userId, e);
                result.addFailed(userId);
            }
        }

        log.info("批量推送消息完成: appId={}, messageId={}, successCount={}, failedCount={}",
                appId, messageBody.getMessageId(), result.getSuccessCount(), result.getFailedCount());

        return result;
    }

    /**
     * 推送到本地会话列表
     */
    private boolean pushToLocalSessions(Integer appId, List<UserSession> sessions, MessageDTO messageBody) {
        boolean allSuccess = true;

        for (UserSession session : sessions) {
            boolean success = pushToLocalSession(appId, session, messageBody);
            allSuccess = allSuccess && success;
        }

        return allSuccess;
    }

    /**
     * 推送到本地单个会话
     */
    private boolean pushToLocalSession(Integer appId, UserSession session, MessageDTO messageBody) {
        try {
            // 使用本地推送工具
            MessagePushUtils.pushMessageToUser(appId, session.getUserId().toString(), messageBody);

            log.debug("本地推送成功: userId={}, clientType={}, imei={}",
                    session.getUserId(), session.getClientType(), session.getImei());
            return true;

        } catch (Exception e) {
            log.error("本地推送失败: userId={}, clientType={}, imei={}",
                    session.getUserId(), session.getClientType(), session.getImei(), e);
            return false;
        }
    }

    /**
     * 推送到远程会话列表
     */
    private boolean pushToRemoteSessions(Integer appId, String serverId, List<UserSession> sessions, MessageDTO messageBody) {
        boolean allSuccess = true;

        for (UserSession session : sessions) {
            boolean success = pushToRemoteSession(appId, session, messageBody);
            allSuccess = allSuccess && success;
        }

        return allSuccess;
    }

    /**
     * 推送到远程单个会话
     */
    private boolean pushToRemoteSession(Integer appId, UserSession session, MessageDTO messageBody) {
        try {

            Address address = new Address(session.getBrokerIp(), Integer.parseInt(session.getBrokerPort()));
            UserSpecifiedAddressUtil.setAddress(address);

            // 调用远程推送服务
            boolean success = imPushService.pushMessageToUserClient(
                    appId,
                    session.getUserId(),
                    session.getClientType(),
                    session.getImei(),
                    messageBody
            );

            log.debug("远程推送结果: userId={}, clientType={}, imei={}, remoteAddress={}, success={}",
                    session.getUserId(), session.getClientType(), session.getImei(), session.getBrokerIp() + ':' + session.getBrokerPort(), success);

            return success;

        } catch (Exception e) {
            log.error("远程推送失败: userId={}, clientType={}, imei={}",
                    session.getUserId(), session.getClientType(), session.getImei(), e);
            return false;
        } finally {
            UserSpecifiedAddressUtil.setAddress(null);
        }
    }

    /**
     * 推送结果统计类
     */
    public static class PushResult {
        private int successCount = 0;
        private int failedCount = 0;
        
        public void addSuccess(Long userId) {
            successCount++;
        }
        
        public void addFailed(Long userId) {
            failedCount++;
        }
        
        public int getSuccessCount() {
            return successCount;
        }
        
        public int getFailedCount() {
            return failedCount;
        }
        
        public int getTotalCount() {
            return successCount + failedCount;
        }
        
        public boolean isAllSuccess() {
            return failedCount == 0;
        }
    }
}