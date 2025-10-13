package com.luna.conversationserver.conversation.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.conversationserver.conversation.domain.entity.Conversation;
import com.luna.conversationserver.conversation.mapper.ConversationMapper;
import com.luna.conversationserver.conversation.enums.ConversationTypeEnum;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConversationDao extends ServiceImpl<ConversationMapper, Conversation> {

    /**
     * 根据创建者和类型查找会话
     */
    public Conversation getByCreatorAndType(Long creatorId, Integer type) {
        return lambdaQuery()
                .eq(Conversation::getCreatorId, creatorId)
                .eq(Conversation::getType, type)
                .eq(Conversation::getStatus, 0)
                .one();
    }

    /**
     * 根据ID获取正常状态的会话
     */
    public Conversation getActiveById(Long conversationId) {
        return lambdaQuery()
                .eq(Conversation::getConversationId, conversationId)
                .eq(Conversation::getStatus, 0)
                .one();
    }

    /**
     * 查找单聊会话（根据两个用户ID）
     * 需要在service层处理两个用户的双向查找
     */
    public Conversation getP2PConversation(Long userId1, Long userId2) {
        // 这个方法需要结合ConversationMember表来查询
        // 实际实现在Service层
        return null;
    }

    /**
     * 根据状态获取会话列表
     */
    public List<Conversation> getByStatus(Integer status) {
        return lambdaQuery()
                .eq(Conversation::getStatus, status)
                .list();
    }

    /**
     * 更新会话状态
     */
    public boolean updateStatus(Long conversationId, Integer status) {
        return lambdaUpdate()
                .eq(Conversation::getConversationId, conversationId)
                .set(Conversation::getStatus, status)
                .update();
    }
}
