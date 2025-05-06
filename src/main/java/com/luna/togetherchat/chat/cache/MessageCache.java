package com.luna.togetherchat.chat.cache;

import com.luna.togetherchat.chat.dao.MessageDao;
import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.common.cache.doubleCache.CacheMethod;
import com.luna.togetherchat.common.cache.doubleCache.DoubleCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Description: 消息相关缓存，syncId -> message
 *
 */

@Component
@RequiredArgsConstructor
public class MessageCache {
    // 冷热数据分离类型 CachePattern.WRITE_THROUGH
    private final MessageDao messageDao;

    @DoubleCache(cacheNames = "message", key = "#msgId", type = CacheMethod.CACHEABLE)
    public Message getMessage(Long msgId) {
        return messageDao.getById(msgId);
    }

    @DoubleCache(cacheNames = "message", key = "#msgId", type = CacheMethod.UPDATE_IF_PRESENT)
    public void updateMessage(Long msgId, Message message) { messageDao.updateById(message); }

    @DoubleCache(cacheNames = "message", key = "#msgId", type = CacheMethod.EVICT)
    public void evictMessage(Long msgId) {}
}
