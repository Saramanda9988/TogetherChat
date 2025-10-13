package com.luna.conversationserver.conversation.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.conversationserver.conversation.domain.entity.ConversationMember;
import com.luna.conversationserver.conversation.mapper.ConversationMemberMapper;
import com.luna.conversationserver.conversation.enums.ConversationMemberStateEnum;
import com.luna.conversationserver.conversation.enums.ConversationRoleEnum;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConversationMemberDao extends ServiceImpl<ConversationMemberMapper, ConversationMember> {

    /**
     * 根据会话ID获取所有正常状态的成员
     */
    public List<ConversationMember> getActiveMembers(Long conversationId) {
        return lambdaQuery()
                .eq(ConversationMember::getConversationId, conversationId)
                .ne(ConversationMember::getState, ConversationMemberStateEnum.BLOCKED.getType())
                .list();
    }

    /**
     * 根据会话ID和用户ID获取成员
     */
    public ConversationMember getMember(Long conversationId, Long userId) {
        return lambdaQuery()
                .eq(ConversationMember::getConversationId, conversationId)
                .eq(ConversationMember::getUserId, userId)
                .one();
    }

    /**
     * 根据用户ID获取其参与的所有会话成员记录
     */
    public List<ConversationMember> getUserConversations(Long userId) {
        return lambdaQuery()
                .eq(ConversationMember::getUserId, userId)
                .ne(ConversationMember::getState, ConversationMemberStateEnum.BLOCKED.getType())
                .list();
    }

    /**
     * 统计会话成员数量（不包括已退出的）
     */
    public Long countActiveMembers(Long conversationId) {
        return lambdaQuery()
                .eq(ConversationMember::getConversationId, conversationId)
                .ne(ConversationMember::getState, ConversationMemberStateEnum.BLOCKED.getType())
                .count();
    }

    /**
     * 检查用户是否是会话成员
     */
    public boolean isMember(Long conversationId, Long userId) {
        ConversationMember member = getMember(conversationId, userId);
        return member != null && !member.getState().equals(ConversationMemberStateEnum.BLOCKED.getType());
    }

    /**
     * 检查用户是否有管理权限（群主或管理员）
     */
    public boolean hasAdminPermission(Long conversationId, Long userId) {
        ConversationMember member = getMember(conversationId, userId);
        if (member == null || member.getState().equals(ConversationMemberStateEnum.BLOCKED.getType())) {
            return false;
        }
        return member.getRole() >= ConversationRoleEnum.ADMIN.getType();
    }

    /**
     * 更新成员状态
     */
    public boolean updateMemberState(Long conversationId, Long userId, Byte state) {
        return lambdaUpdate()
                .eq(ConversationMember::getConversationId, conversationId)
                .eq(ConversationMember::getUserId, userId)
                .set(ConversationMember::getState, state)
                .update();
    }

    /**
     * 更新成员角色
     */
    public boolean updateMemberRole(Long conversationId, Long userId, Integer role) {
        return lambdaUpdate()
                .eq(ConversationMember::getConversationId, conversationId)
                .eq(ConversationMember::getUserId, userId)
                .set(ConversationMember::getRole, role)
                .update();
    }

    /**
     * 移除成员（设置为退出状态）
     */
    public boolean removeMember(Long conversationId, Long userId) {
        return updateMemberState(conversationId, userId, ConversationMemberStateEnum.BLOCKED.getType().byteValue());
    }

    /**
     * 查找单聊会话中的两个用户
     */
    public List<ConversationMember> getP2PMembers(Long userId1, Long userId2) {
        // 查找包含这两个用户的单聊会话
        List<Long> conversationIds = lambdaQuery()
                .eq(ConversationMember::getUserId, userId1)
                .ne(ConversationMember::getState, ConversationMemberStateEnum.BLOCKED.getType())
                .list()
                .stream()
                .map(ConversationMember::getConversationId)
                .toList();

        if (conversationIds.isEmpty()) {
            return List.of();
        }

        return lambdaQuery()
                .in(ConversationMember::getConversationId, conversationIds)
                .eq(ConversationMember::getUserId, userId2)
                .ne(ConversationMember::getState, ConversationMemberStateEnum.BLOCKED.getType())
                .list();
    }
}
