package com.luna.imtcp.service.dubbo;

import com.luna.imtcp.api.service.ImPushService;
import com.luna.imtcp.utils.MessagePushUtils;
import com.luna.imtcp.utils.UserChannelUtils;
import com.luna.messageserver.api.dto.MessageDTO;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * IM推送服务实现类
 */
@Slf4j
@Service
@DubboService(interfaceClass = ImPushService.class, group = "im-push-service")
public class ImPushServiceImpl implements ImPushService {

    @Override
    public boolean pushMessageToUser(Integer appId, Long userId, MessageDTO messageBody) {
        try {
            log.info("尝试推送消息给用户: appId={}, userId={}, messageId={}", 
                    appId, userId, messageBody.getMessageId());
            
            // 直接使用本地推送工具
            MessagePushUtils.pushMessageToUser(appId, userId.toString(), messageBody);
            return true;
            
        } catch (Exception e) {
            log.error("推送消息失败: appId={}, userId={}, error={}", appId, userId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean pushMessageToUserClient(Integer appId, Long userId, Integer clientType, String imei, MessageDTO messageBody) {
        try {
            log.info("尝试推送消息给用户客户端: appId={}, userId={}, clientType={}, imei={}, messageId={}", 
                    appId, userId, clientType, imei, messageBody.getMessageId());
            
            // 获取指定客户端的Channel
            Channel channel = UserChannelUtils.getUserChannel(appId, userId, clientType, imei);
            if (channel != null) {
                MessagePushUtils.pushMessageToChannel(channel, appId, messageBody);
                return true;
            } else {
                log.warn("用户客户端不在当前服务实例上: appId={}, userId={}, clientType={}, imei={}", 
                        appId, userId, clientType, imei);
                return false;
            }
            
        } catch (Exception e) {
            log.error("推送消息给用户客户端失败: appId={}, userId={}, clientType={}, imei={}, error={}", 
                    appId, userId, clientType, imei, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isUserOnline(Integer appId, Long userId) {
        try {
            return !UserChannelUtils.getUserChannels(appId, userId.toString()).isEmpty();
        } catch (Exception e) {
            log.error("检查用户在线状态失败: appId={}, userId={}, error={}", appId, userId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isUserClientOnline(Integer appId, Long userId, Integer clientType, String imei) {
        try {
            Channel channel = UserChannelUtils.getUserChannel(appId, userId, clientType, imei);
            return channel != null && channel.isActive();
        } catch (Exception e) {
            log.error("检查用户客户端在线状态失败: appId={}, userId={}, clientType={}, imei={}, error={}", 
                    appId, userId, clientType, imei, e.getMessage(), e);
            return false;
        }
    }
}