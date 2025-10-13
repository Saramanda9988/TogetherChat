package com.luna.conversationserver.conversation.service;

import com.luna.common.exception.BusinessException;
import com.luna.conversationserver.conversation.dao.ConversationDao;
import com.luna.conversationserver.conversation.dao.ConversationMemberDao;
import com.luna.conversationserver.conversation.domain.entity.Conversation;
import com.luna.conversationserver.conversation.domain.entity.ConversationMember;
import com.luna.conversationserver.conversation.domain.request.CreateGroupRequest;
import com.luna.conversationserver.conversation.domain.request.CreateP2PRequest;
import com.luna.conversationserver.conversation.domain.request.UpdateConversationRequest;
import com.luna.conversationserver.conversation.domain.response.ConversationResponse;
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
 *  会话服务类
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-10-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationDao conversationDao;
    private final ConversationMemberDao conversationMemberDao;

    /**
     * 创建或获取单聊会话
     */
    @Transactional
    public ConversationResponse createOrGetP2P(CreateP2PRequest request, Long currentUserId) {
        if (currentUserId.equals(request.getTargetUserId())) {
            throw new BusinessException("不能与自己创建单聊");
        }

        // 查找是否已存在单聊会话
        List<ConversationMember> existingMembers = conversationMemberDao.getP2PMembers(currentUserId, request.getTargetUserId());
        
        if (!existingMembers.isEmpty()) {
            // 已存在单聊会话，返回现有会话
            Long conversationId = existingMembers.get(0).getConversationId();
            Conversation conversation = conversationDao.getActiveById(conversationId);
            if (conversation != null) {
                return buildConversationResponse(conversation);
            }
        }

        // 创建新的单聊会话
        Conversation conversation = Conversation.builder()
                .name("单聊")
                .creatorId(currentUserId)
                .type(ConversationTypeEnum.P2P.getType())
                .status(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        conversationDao.save(conversation);

        // 添加两个成员
        ConversationMember creatorMember = ConversationMember.builder()
                .conversationId(conversation.getConversationId())
                .userId(currentUserId)
                .role(ConversationRoleEnum.SINGLE.getType())
                .joinTime(LocalDateTime.now())
                .state(ConversationMemberStateEnum.NORMAL.getType().byteValue())
                .build();

        ConversationMember targetMember = ConversationMember.builder()
                .conversationId(conversation.getConversationId())
                .userId(request.getTargetUserId())
                .role(ConversationRoleEnum.SINGLE.getType())
                .joinTime(LocalDateTime.now())
                .state(ConversationMemberStateEnum.NORMAL.getType().byteValue())
                .build();

        conversationMemberDao.save(creatorMember);
        conversationMemberDao.save(targetMember);

        log.info("创建单聊会话成功, conversationId: {}, creator: {}, target: {}",
                conversation.getConversationId(), currentUserId, request.getTargetUserId());

        return buildConversationResponse(conversation);
    }

    /**
     * 创建群聊
     */
    @Transactional
    public ConversationResponse createGroup(CreateGroupRequest request, Long currentUserId) {
        // 创建群聊会话
        Conversation conversation = Conversation.builder()
                .name(request.getName())
                .creatorId(currentUserId)
                .description(request.getDescription())
                .avatar(request.getAvatar())
                .type(ConversationTypeEnum.GROUP.getType())
                .status(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        conversationDao.save(conversation);

        // 添加创建者为群主
        ConversationMember ownerMember = ConversationMember.builder()
                .conversationId(conversation.getConversationId())
                .userId(currentUserId)
                .role(ConversationRoleEnum.OWNER.getType())
                .joinTime(LocalDateTime.now())
                .state(ConversationMemberStateEnum.NORMAL.getType().byteValue())
                .build();

        conversationMemberDao.save(ownerMember);

        // 添加初始成员
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            List<ConversationMember> members = request.getMemberIds().stream()
                    .filter(memberId -> !memberId.equals(currentUserId)) // 排除创建者
                    .map(memberId -> ConversationMember.builder()
                            .conversationId(conversation.getConversationId())
                            .userId(memberId)
                            .role(ConversationRoleEnum.MEMBER.getType())
                            .joinTime(LocalDateTime.now())
                            .state(ConversationMemberStateEnum.NORMAL.getType().byteValue())
                            .build())
                    .collect(Collectors.toList());

            conversationMemberDao.saveBatch(members);
        }

        log.info("创建群聊成功, conversationId: {}, name: {}, creator: {}",
                conversation.getConversationId(), conversation.getName(), currentUserId);

        return buildConversationResponse(conversation);
    }

    /**
     * 获取会话详情
     */
    public ConversationResponse getConversationInfo(Long conversationId, Long currentUserId) {
        // 检查用户是否是会话成员
        if (!conversationMemberDao.isMember(conversationId, currentUserId)) {
            throw new BusinessException("无权访问该会话");
        }

        Conversation conversation = conversationDao.getActiveById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }

        return buildConversationResponse(conversation);
    }

    /**
     * 查询用户的所有会话
     */
    public List<ConversationResponse> getUserConversations(Long userId) {
        List<ConversationMember> memberList = conversationMemberDao.getUserConversations(userId);
        
        List<Long> conversationIds = memberList.stream()
                .map(ConversationMember::getConversationId)
                .collect(Collectors.toList());

        if (conversationIds.isEmpty()) {
            return List.of();
        }

        List<Conversation> conversations = conversationDao.listByIds(conversationIds);
        
        return conversations.stream()
                .filter(conv -> conv.getStatus() == 0) // 只返回正常状态的会话
                .map(this::buildConversationResponse)
                .collect(Collectors.toList());
    }

    /**
     * 更新群聊信息
     */
    @Transactional
    public ConversationResponse updateConversation(Long conversationId, UpdateConversationRequest request, Long currentUserId) {
        Conversation conversation = conversationDao.getActiveById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }

        // 检查权限：只有群主和管理员可以修改群聊信息
        if (conversation.getType().equals(ConversationTypeEnum.GROUP.getType()) &&
                !conversationMemberDao.hasAdminPermission(conversationId, currentUserId)) {
            throw new BusinessException("无权修改群聊信息");
        }

        // 更新会话信息
        boolean updated = false;
        if (request.getName() != null && !request.getName().equals(conversation.getName())) {
            conversation.setName(request.getName());
            updated = true;
        }
        if (request.getDescription() != null && !request.getDescription().equals(conversation.getDescription())) {
            conversation.setDescription(request.getDescription());
            updated = true;
        }
        if (request.getAvatar() != null && !request.getAvatar().equals(conversation.getAvatar())) {
            conversation.setAvatar(request.getAvatar());
            updated = true;
        }

        if (updated) {
            conversation.setUpdateTime(LocalDateTime.now());
            conversationDao.updateById(conversation);
            log.info("更新会话信息成功, conversationId: {}, userId: {}", conversationId, currentUserId);
        }

        return buildConversationResponse(conversation);
    }

    /**
     * 解散群聊
     */
    @Transactional
    public void deleteConversation(Long conversationId, Long currentUserId) {
        Conversation conversation = conversationDao.getActiveById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }

        // 单聊不能解散
        if (conversation.getType().equals(ConversationTypeEnum.P2P.getType())) {
            throw new BusinessException("单聊会话不能解散");
        }

        // 检查权限：只有群主可以解散群聊
        ConversationMember member = conversationMemberDao.getMember(conversationId, currentUserId);
        if (member == null || !member.getRole().equals(ConversationRoleEnum.OWNER.getType())) {
            throw new BusinessException("只有群主可以解散群聊");
        }

        // 标记会话为删除状态
        conversationDao.updateStatus(conversationId, 1);

        // 将所有成员状态设置为退出
        List<ConversationMember> members = conversationMemberDao.getActiveMembers(conversationId);
        members.forEach(m -> conversationMemberDao.updateMemberState(conversationId, m.getUserId(), ConversationMemberStateEnum.BLOCKED.getType().byteValue()));

        log.info("解散群聊成功, conversationId: {}, userId: {}", conversationId, currentUserId);
    }

    /**
     * 构建会话响应对象
     */
    private ConversationResponse buildConversationResponse(Conversation conversation) {
        Long memberCount = conversationMemberDao.countActiveMembers(conversation.getConversationId());
        
        return ConversationResponse.builder()
                .conversationId(conversation.getConversationId())
                .name(conversation.getName())
                .creatorId(conversation.getCreatorId())
                .description(conversation.getDescription())
                .avatar(conversation.getAvatar())
                .createTime(conversation.getCreateTime())
                .updateTime(conversation.getUpdateTime())
                .type(conversation.getType())
                .status(conversation.getStatus())
                .memberCount(memberCount.intValue())
                .build();
    }
}
