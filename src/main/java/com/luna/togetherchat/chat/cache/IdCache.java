package com.luna.togetherchat.chat.cache;

import com.luna.togetherchat.chat.dao.MessageDao;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class IdCache {
    private final RedisTemplate<String, Object> redisTemplate;
    private final MessageDao messageDao;

    @PostConstruct
    private void init() {
        Map<Long, Long> lastIdMap = messageDao.getLastIdMap();
        lastIdMap.forEach((k, v) -> redisTemplate.opsForValue().set("roomID:" + k, v));
        redisTemplate.opsForValue().set("messageID:", messageDao.getLargestMessageId());
    }

    public Long getNextSyncId(Long roomId) {
        return redisTemplate.opsForValue().increment("roomID:" + roomId);
    }

    public Long getNextMessageId() {
        return redisTemplate.opsForValue().increment("messageID:");
    }

}
