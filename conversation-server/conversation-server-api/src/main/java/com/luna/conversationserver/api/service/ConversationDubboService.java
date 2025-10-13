package com.luna.conversationserver.api.service;

import com.luna.conversationserver.api.dto.ConversationDTO;
import com.luna.conversationserver.api.dto.ConversationMemberDTO;

import java.util.List;

/**
 * 会话服务Dubbo接口定义
 * 提供会话相关的远程调用服务
 */
public interface ConversationDubboService {

    /**
     * 根据会话ID查询会话信息
     * @param conversationId 会话ID
     * @return 会话信息
     */
    ConversationDTO getConversationById(Long conversationId);

    /**
     * 根据用户ID获取其参与的所有会话
     * @param userId 用户ID
     * @return 会话列表
     */
    List<ConversationDTO> getUserConversations(Long userId);

    /**
     * 检查用户是否是会话成员
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 是否是成员
     */
    boolean isMember(Long conversationId, Long userId);

    /**
     * 获取会话成员列表
     * @param conversationId 会话ID
     * @return 成员列表
     */
    List<ConversationMemberDTO> getConversationMembers(Long conversationId);

    /**
     * 获取会话成员数量
     * @param conversationId 会话ID
     * @return 成员数量
     */
    Integer getMemberCount(Long conversationId);

    /**
     * 检查用户在会话中的角色权限
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 角色类型：0=单聊，1=成员，2=管理员，3=群主
     */
    Integer getUserRole(Long conversationId, Long userId);
}