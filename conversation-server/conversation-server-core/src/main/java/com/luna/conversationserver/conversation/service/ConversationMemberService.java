package com.luna.conversationserver.conversation.service;

import com.luna.common.exception.BusinessException;
import com.luna.conversationserver.conversation.dao.ConversationDao;
import com.luna.conversationserver.conversation.dao.ConversationMemberDao;
import com.luna.conversationserver.conversation.domain.entity.Conversation;
import com.luna.conversationserver.conversation.domain.entity.ConversationMember;
import com.luna.conversationserver.conversation.domain.request.AddMemberRequest;
import com.luna.conversationserver.conversation.domain.request.BatchAddMemberRequest;
import com.luna.conversationserver.conversation.domain.request.MuteMemberRequest;
import com.luna.conversationserver.conversation.domain.request.UpdateMemberRoleRequest;
import com.luna.conversationserver.conversation.domain.response.ConversationMemberResponse;
import com.luna.conversationserver.conversation.enums.ConversationMemberStateEnum;
import com.luna.conversationserver.conversation.enums.ConversationRoleEnum;
import com.luna.conversationserver.conversation.enums.ConversationTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  会话成员服务类
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-10-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationMemberService {

    private final ConversationDao conversationDao;
    private final ConversationMemberDao conversationMemberDao;

    /**
     * 添加成员
     */
    @Transactional
    public ConversationMemberResponse addMember(AddMemberRequest request, Long currentUserId) {
        // 验证会话存在且为群聊
        Conversation conversation = conversationDao.getActiveById(request.getConversationId());
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }
        if (!conversation.getType().equals(ConversationTypeEnum.GROUP.getType())) {
            throw new BusinessException("只有群聊才能添加成员");
        }

        // 检查操作权限
        if (!conversationMemberDao.hasAdminPermission(request.getConversationId(), currentUserId)) {
            throw new BusinessException("无权添加成员");
        }

        // 检查用户是否已是成员
        if (conversationMemberDao.isMember(request.getConversationId(), request.getUserId())) {
            throw new BusinessException("用户已是群聊成员");
        }

        // 添加成员
        ConversationMember member = ConversationMember.builder()
                .conversationId(request.getConversationId())
                .userId(request.getUserId())
                .role(ConversationRoleEnum.MEMBER.getType())
                .joinTime(LocalDateTime.now())
                .nickname(request.getNickname())
                .state(ConversationMemberStateEnum.NORMAL.getType().byteValue())
                .build();

        conversationMemberDao.save(member);

        log.info("添加成员成功, conversationId: {}, userId: {}, addedBy: {}",
                request.getConversationId(), request.getUserId(), currentUserId);

        return buildMemberResponse(member);
    }

    /**
     * 批量添加成员
     */
    @Transactional
    public List<ConversationMemberResponse> batchAddMembers(BatchAddMemberRequest request, Long currentUserId) {
        // 验证会话存在且为群聊
        Conversation conversation = conversationDao.getActiveById(request.getConversationId());
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }
        if (!conversation.getType().equals(ConversationTypeEnum.GROUP.getType())) {
            throw new BusinessException("只有群聊才能添加成员");
        }

        // 检查操作权限
        if (!conversationMemberDao.hasAdminPermission(request.getConversationId(), currentUserId)) {
            throw new BusinessException("无权添加成员");
        }

        // 过滤掉已经是成员的用户
        List<Long> newMemberIds = request.getUserIds().stream()
                .filter(userId -> !conversationMemberDao.isMember(request.getConversationId(), userId))
                .collect(Collectors.toList());

        if (newMemberIds.isEmpty()) {
            throw new BusinessException("所有用户都已是群聊成员");
        }

        // 批量添加成员
        List<ConversationMember> members = newMemberIds.stream()
                .map(userId -> ConversationMember.builder()
                        .conversationId(request.getConversationId())
                        .userId(userId)
                        .role(ConversationRoleEnum.MEMBER.getType())
                        .joinTime(LocalDateTime.now())
                        .state(ConversationMemberStateEnum.NORMAL.getType().byteValue())
                        .build())
                .collect(Collectors.toList());

        conversationMemberDao.saveBatch(members);

        log.info("批量添加成员成功, conversationId: {}, addedCount: {}, addedBy: {}",
                request.getConversationId(), members.size(), currentUserId);

        return members.stream()
                .map(this::buildMemberResponse)
                .collect(Collectors.toList());
    }

    /**
     * 移除成员
     */
    @Transactional
    public void removeMember(Long conversationId, Long userId, Long currentUserId) {
        // 验证会话存在
        Conversation conversation = conversationDao.getActiveById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }
        if (!conversation.getType().equals(ConversationTypeEnum.GROUP.getType())) {
            throw new BusinessException("只有群聊才能移除成员");
        }

        // 检查被移除的成员是否存在
        ConversationMember targetMember = conversationMemberDao.getMember(conversationId, userId);
        if (targetMember == null || targetMember.getState().equals(ConversationMemberStateEnum.BLOCKED.getType().byteValue())) {
            throw new BusinessException("用户不是群聊成员");
        }

        // 检查操作权限
        ConversationMember currentMember = conversationMemberDao.getMember(conversationId, currentUserId);
        if (currentMember == null) {
            throw new BusinessException("无权操作");
        }

        // 群主不能被移除
        if (targetMember.getRole().equals(ConversationRoleEnum.OWNER.getType())) {
            throw new BusinessException("群主不能被移除");
        }

        // 只有群主可以移除管理员，管理员和群主可以移除普通成员
        if (targetMember.getRole().equals(ConversationRoleEnum.ADMIN.getType()) &&
                !currentMember.getRole().equals(ConversationRoleEnum.OWNER.getType())) {
            throw new BusinessException("只有群主可以移除管理员");
        }

        if (!conversationMemberDao.hasAdminPermission(conversationId, currentUserId)) {
            throw new BusinessException("无权移除成员");
        }

        // 移除成员
        conversationMemberDao.removeMember(conversationId, userId);

        log.info("移除成员成功, conversationId: {}, userId: {}, removedBy: {}",
                conversationId, userId, currentUserId);
    }

    /**
     * 获取成员列表
     */
    public List<ConversationMemberResponse> getMembers(Long conversationId, Long currentUserId) {
        // 检查用户是否是会话成员
        if (!conversationMemberDao.isMember(conversationId, currentUserId)) {
            throw new BusinessException("无权查看成员列表");
        }

        List<ConversationMember> members = conversationMemberDao.getActiveMembers(conversationId);
        
        return members.stream()
                .map(this::buildMemberResponse)
                .collect(Collectors.toList());
    }

    /**
     * 退出群聊
     */
    @Transactional
    public void quitConversation(Long conversationId, Long currentUserId) {
        // 验证会话存在
        Conversation conversation = conversationDao.getActiveById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }
        if (!conversation.getType().equals(ConversationTypeEnum.GROUP.getType())) {
            throw new BusinessException("单聊不能退出");
        }

        // 检查用户是否是成员
        ConversationMember member = conversationMemberDao.getMember(conversationId, currentUserId);
        if (member == null || member.getState().equals(ConversationMemberStateEnum.BLOCKED.getType().byteValue())) {
            throw new BusinessException("您不是群聊成员");
        }

        // 群主不能直接退出，需要先转让群主
        if (member.getRole().equals(ConversationRoleEnum.OWNER.getType())) {
            throw new BusinessException("群主需要先转让群主权限才能退出");
        }

        // 退出群聊
        conversationMemberDao.removeMember(conversationId, currentUserId);

        log.info("退出群聊成功, conversationId: {}, userId: {}", conversationId, currentUserId);
    }

    /**
     * 修改成员角色
     */
    @Transactional
    public ConversationMemberResponse updateMemberRole(Long conversationId, Long userId, UpdateMemberRoleRequest request, Long currentUserId) {
        // 验证会话存在
        Conversation conversation = conversationDao.getActiveById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }
        if (!conversation.getType().equals(ConversationTypeEnum.GROUP.getType())) {
            throw new BusinessException("只有群聊才能修改成员角色");
        }

        // 检查目标成员是否存在
        ConversationMember targetMember = conversationMemberDao.getMember(conversationId, userId);
        if (targetMember == null || targetMember.getState().equals(ConversationMemberStateEnum.BLOCKED.getType().byteValue())) {
            throw new BusinessException("用户不是群聊成员");
        }

        // 检查操作权限：只有群主可以修改角色
        ConversationMember currentMember = conversationMemberDao.getMember(conversationId, currentUserId);
        if (currentMember == null || !currentMember.getRole().equals(ConversationRoleEnum.OWNER.getType())) {
            throw new BusinessException("只有群主可以修改成员角色");
        }

        // 不能修改自己的角色
        if (userId.equals(currentUserId)) {
            throw new BusinessException("不能修改自己的角色");
        }

        // 验证角色值
        if (!request.getRole().equals(ConversationRoleEnum.MEMBER.getType()) &&
                !request.getRole().equals(ConversationRoleEnum.ADMIN.getType()) &&
                !request.getRole().equals(ConversationRoleEnum.OWNER.getType())) {
            throw new BusinessException("无效的角色类型");
        }

        // 转让群主
        if (request.getRole().equals(ConversationRoleEnum.OWNER.getType())) {
            // 将当前群主降为普通成员
            conversationMemberDao.updateMemberRole(conversationId, currentUserId, ConversationRoleEnum.MEMBER.getType());
            log.info("转让群主权限, conversationId: {}, from: {}, to: {}", conversationId, currentUserId, userId);
        }

        // 更新目标成员角色
        conversationMemberDao.updateMemberRole(conversationId, userId, request.getRole());

        // 获取更新后的成员信息
        ConversationMember updatedMember = conversationMemberDao.getMember(conversationId, userId);

        log.info("修改成员角色成功, conversationId: {}, userId: {}, newRole: {}, operatedBy: {}",
                conversationId, userId, request.getRole(), currentUserId);

        return buildMemberResponse(updatedMember);
    }

    /**
     * 禁言/解除禁言成员
     */
    @Transactional
    public ConversationMemberResponse muteMember(Long conversationId, Long userId, MuteMemberRequest request, Long currentUserId) {
        // 验证会话存在
        Conversation conversation = conversationDao.getActiveById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }
        if (!conversation.getType().equals(ConversationTypeEnum.GROUP.getType())) {
            throw new BusinessException("只有群聊才能禁言成员");
        }

        // 检查目标成员是否存在
        ConversationMember targetMember = conversationMemberDao.getMember(conversationId, userId);
        if (targetMember == null || targetMember.getState().equals(ConversationMemberStateEnum.BLOCKED.getType().byteValue())) {
            throw new BusinessException("用户不是群聊成员");
        }

        // 检查操作权限
        if (!conversationMemberDao.hasAdminPermission(conversationId, currentUserId)) {
            throw new BusinessException("无权禁言成员");
        }

        // 不能禁言群主和管理员（除非操作者是群主）
        ConversationMember currentMember = conversationMemberDao.getMember(conversationId, currentUserId);
        if (targetMember.getRole() >= ConversationRoleEnum.ADMIN.getType() &&
                !currentMember.getRole().equals(ConversationRoleEnum.OWNER.getType())) {
            throw new BusinessException("只有群主可以禁言管理员");
        }

        // 不能禁言自己
        if (userId.equals(currentUserId)) {
            throw new BusinessException("不能禁言自己");
        }

        // 更新禁言状态
        Byte newState = request.getMuted() ?
                ConversationMemberStateEnum.MUTED.getType().byteValue() :
                ConversationMemberStateEnum.NORMAL.getType().byteValue();

        conversationMemberDao.updateMemberState(conversationId, userId, newState);

        // 获取更新后的成员信息
        ConversationMember updatedMember = conversationMemberDao.getMember(conversationId, userId);

        log.info("{}成员成功, conversationId: {}, userId: {}, operatedBy: {}",
                request.getMuted() ? "禁言" : "解除禁言", conversationId, userId, currentUserId);

        return buildMemberResponse(updatedMember);
    }

    /**
     * 构建成员响应对象
     */
    private ConversationMemberResponse buildMemberResponse(ConversationMember member) {
        return ConversationMemberResponse.builder()
                .memberId(member.getMemberId())
                .conversationId(member.getConversationId())
                .userId(member.getUserId())
                .role(member.getRole())
                .joinTime(member.getJoinTime())
                .nickname(member.getNickname())
                .state(member.getState())
                .build();
    }
}
