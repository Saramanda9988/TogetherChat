package com.luna.conversationserver.conversation.service.dubbo;

import com.luna.conversationserver.api.dto.ConversationDTO;
import com.luna.conversationserver.api.dto.ConversationMemberDTO;
import com.luna.conversationserver.api.service.ConversationDubboService;
import com.luna.conversationserver.conversation.dao.ConversationDao;
import com.luna.conversationserver.conversation.dao.ConversationMemberDao;
import com.luna.conversationserver.conversation.domain.entity.Conversation;
import com.luna.conversationserver.conversation.domain.entity.ConversationMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会话服务Dubbo实现类
 * 提供会话相关的远程调用服务实现
 *
 * @author LunaRain_079
 * @since 2025-10-13
 */
@Slf4j
@Component
@RequiredArgsConstructor
@DubboService(group = "conversation-service", interfaceClass = ConversationDubboService.class)
@Service
public class ConversationDubboServiceImpl implements ConversationDubboService {

    private final ConversationDao conversationDao;
    private final ConversationMemberDao conversationMemberDao;

    /**
     * 根据会话ID查询会话信息
     */
    @Override
    public ConversationDTO getConversationById(Long conversationId) {
        Conversation conversation = conversationDao.getActiveById(conversationId);
        if (conversation == null) {
            return null;
        }
        return convertToDTO(conversation);
    }

    /**
     * 根据用户ID获取其参与的所有会话
     */
    @Override
    public List<ConversationDTO> getUserConversations(Long userId) {
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
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 检查用户是否是会话成员
     */
    @Override
    public boolean isMember(Long conversationId, Long userId) {
        return conversationMemberDao.isMember(conversationId, userId);
    }

    /**
     * 获取会话成员列表
     */
    @Override
    public List<ConversationMemberDTO> getConversationMembers(Long conversationId) {
        List<ConversationMember> members = conversationMemberDao.getActiveMembers(conversationId);
        return members.stream()
                .map(this::convertToMemberDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取会话成员数量
     */
    @Override
    public Integer getMemberCount(Long conversationId) {
        Long count = conversationMemberDao.countActiveMembers(conversationId);
        return count.intValue();
    }

    /**
     * 检查用户在会话中的角色权限
     */
    @Override
    public Integer getUserRole(Long conversationId, Long userId) {
        ConversationMember member = conversationMemberDao.getMember(conversationId, userId);
        if (member == null) {
            return null;
        }
        return member.getRole();
    }

    /**
     * 获取所有会话ID
     *
     * @return
     */
    @Override
    public List<Long> getAllConversationIds() {
        return conversationDao.getAllConversationIds()
                .stream()
                .map(Conversation::getConversationId)
                .toList();
    }

    /**
     * 转换为ConversationDTO
     */
    private ConversationDTO convertToDTO(Conversation conversation) {
        Long memberCount = conversationMemberDao.countActiveMembers(conversation.getConversationId());
        
        return ConversationDTO.builder()
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

    /**
     * 转换为ConversationMemberDTO
     */
    private ConversationMemberDTO convertToMemberDTO(ConversationMember member) {
        return ConversationMemberDTO.builder()
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