package com.luna.imtcp.api.service;

import com.luna.messageserver.api.dto.MessageDTO;

/**
 * IM推送服务Dubbo接口
 * 提供分布式环境下的消息推送能力
 */
public interface ImPushService {

    /**
     * 推送消息给指定用户
     * 
     * @param appId 应用ID
     * @param userId 用户ID
     * @param messageBody 消息体
     * @return 是否推送成功
     */
    boolean pushMessageToUser(Integer appId, Long userId, MessageDTO messageBody);

    /**
     * 推送消息给指定用户的特定客户端
     * 
     * @param appId 应用ID
     * @param userId 用户ID
     * @param clientType 客户端类型
     * @param imei 设备标识
     * @param messageBody 消息体
     * @return 是否推送成功
     */
    boolean pushMessageToUserClient(Integer appId, Long userId, Integer clientType, String imei, MessageDTO messageBody);

    /**
     * 检查用户是否在当前服务实例上在线
     * 
     * @param appId 应用ID
     * @param userId 用户ID
     * @return 是否在线
     */
    boolean isUserOnline(Integer appId, Long userId);

    /**
     * 检查用户的特定客户端是否在当前服务实例上在线
     * 
     * @param appId 应用ID
     * @param userId 用户ID
     * @param clientType 客户端类型
     * @param imei 设备标识
     * @return 是否在线
     */
    boolean isUserClientOnline(Integer appId, Long userId, Integer clientType, String imei);
}