package com.luna.imtcp.utils;

import com.luna.common.utils.JsonUtils;
import com.luna.imtcp.api.enums.CommandType;
import com.luna.imtcp.api.vo.MessageHeader;
import com.luna.imtcp.api.vo.WebMessage;
import com.luna.imtcp.api.vo.msgBody.MessageVOBody;
import com.luna.messageserver.api.dto.MessageDTO;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 消息推送工具类
 * 负责向WebSocket客户端推送消息
 */
@Slf4j
public class MessagePushUtils {

    private static final String SERVER_IMEI = "server";

    /**
     * 向指定用户推送消息 (支持MessageDTO，保持向后兼容)
     * 
     * @param appId 应用ID
     * @param userId 用户ID
     * @param messageBody 消息体
     */
    public static void pushMessageToUser(Integer appId, String userId, MessageDTO messageBody) {
        try {
            // 获取用户的所有在线通道
            List<Channel> channels = UserChannelUtils.getUserChannels(appId, userId);
            
            if (channels.isEmpty()) {
                log.info("用户 {} 不在线，无法推送消息", userId);
                return;
            }

            // 构建WebMessage
            WebMessage webMessage = buildWebMessage(appId, messageBody);
            
            // 向每个通道推送消息
            for (Channel channel : channels) {
                if (channel.isActive()) {
                    channel.writeAndFlush(webMessage);
                    log.info("成功推送消息到用户 {}, channelId: {}", userId, channel.id().asShortText());
                } else {
                    log.warn("通道已断开，无法推送消息到用户 {}, channelId: {}", userId, channel.id().asShortText());
                }
            }
            
        } catch (Exception e) {
            log.error("推送消息失败, userId: {}, messageBody: {}", userId, messageBody, e);
        }
    }

    /**
     * 向指定通道推送消息
     * 
     * @param channel 通道
     * @param appId 应用ID
     * @param messageBody 消息体
     */
    public static void pushMessageToChannel(Channel channel, Integer appId, MessageDTO messageBody) {
        try {
            if (channel == null || !channel.isActive()) {
                log.warn("通道不可用，无法推送消息");
                return;
            }

            // 构建WebMessage
            WebMessage webMessage = buildWebMessage(appId, messageBody);
            
            // 推送消息
            channel.writeAndFlush(webMessage);
            log.info("成功推送消息到通道: {}", channel.id().asShortText());
            
        } catch (Exception e) {
            log.error("推送消息到通道失败, channelId: {}, messageBody: {}", 
                    channel != null ? channel.id().asShortText() : "null", messageBody, e);
        }
    }

    /**
     * 构建WebMessage (支持MessageDTO，保持向后兼容)
     * 
     * @param appId 应用ID
     * @param messageBody 消息体
     * @return WebMessage
     */
    private static WebMessage buildWebMessage(Integer appId, MessageDTO messageBody) {
        // 构建消息头
        MessageHeader messageHeader = MessageHeader.builder()
                .command(CommandType.SINGLE_MESSAGE.getType())
                .version(1)
                .clientType(0)
                .messageType(messageBody.getMessageType())
                .appId(appId)
                .imei(SERVER_IMEI) // 服务端推送标识
                .build();

        // 构建WebMessage
        WebMessage webMessage = new WebMessage();
        webMessage.setMessageHeader(messageHeader);
        webMessage.setMessagePack(JsonUtils.toStr(messageBody));
        
        return webMessage;
    }

    /**
     * 批量推送消息给多个用户
     * 
     * @param appId 应用ID
     * @param userIds 用户ID列表
     * @param messageBody 消息体
     */
    public static void pushMessageToUsers(Integer appId, List<String> userIds, MessageDTO messageBody) {
        if (userIds == null || userIds.isEmpty()) {
            log.warn("用户ID列表为空，无法推送消息");
            return;
        }
        
        for (String userId : userIds) {
            pushMessageToUser(appId, userId, messageBody);
        }
    }
}