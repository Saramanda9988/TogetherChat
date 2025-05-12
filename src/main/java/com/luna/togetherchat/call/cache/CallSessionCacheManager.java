package com.luna.togetherchat.call.cache;

import com.luna.togetherchat.common.constant.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 通话会话缓存管理器
 * 使用现有的RedisKey常量管理通话会话
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallSessionCacheManager {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final long SESSION_EXPIRE_TIME = 24 * 60 * 60; // 24小时
    
    /**
     * 用户加入会话
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 是否成功加入（如果用户已在其他会话中，返回false）
     */
    public boolean joinSession(Long userId, Long sessionId) {
        String userKey = RedisKey.getUserCallSessionKey(userId);
        String sessionKey = RedisKey.getCallSessionMembersKey(sessionId);
        
        // 检查用户是否已在其他会话中
        Object existingSessionId = redisTemplate.opsForValue().get(userKey);
        if (existingSessionId != null && !existingSessionId.equals(sessionId)) {
            log.warn("用户{}已在会话{}中，不能加入新会话{}", userId, existingSessionId, sessionId);
            return false;
        }
        
        // 将用户加入会话
        redisTemplate.opsForValue().set(userKey, sessionId, SESSION_EXPIRE_TIME, TimeUnit.SECONDS);
        redisTemplate.opsForSet().add(sessionKey, userId);
        redisTemplate.expire(sessionKey, SESSION_EXPIRE_TIME, TimeUnit.SECONDS);
        
        log.info("用户{}加入会话{}", userId, sessionId);
        return true;
    }
    
    /**
     * 用户离开会话
     * @param userId 用户ID
     * @param sessionId 会话ID
     */
    public void leaveSession(Long userId, Long sessionId) {
        String userKey = RedisKey.getUserCallSessionKey(userId);
        String sessionKey = RedisKey.getCallSessionMembersKey(sessionId);
        
        // 移除用户与会话的关联
        redisTemplate.delete(userKey);
        redisTemplate.opsForSet().remove(sessionKey, userId);
        
        // 检查会话是否还有参与者
        Long size = redisTemplate.opsForSet().size(sessionKey);
        if (size != null && size == 0) {
            redisTemplate.delete(sessionKey);
            log.info("会话{}已无参与者，已清理", sessionId);
        }
        
        log.info("用户{}离开会话{}", userId, sessionId);
    }
    
    /**
     * 获取用户当前所在的会话ID
     * @param userId 用户ID
     * @return 会话ID，如果用户不在任何会话中则返回null
     */
    public Long getUserSessionId(Long userId) {
        String userKey = RedisKey.getUserCallSessionKey(userId);
        Object sessionId = redisTemplate.opsForValue().get(userKey);
        return sessionId != null ? (Long) sessionId : null;
    }
    
    /**
     * 获取会话中的所有用户ID
     * @param sessionId 会话ID
     * @return 用户ID集合
     */
    public Set<Object> getSessionUsers(Long sessionId) {
        String sessionKey = RedisKey.getCallSessionMembersKey(sessionId);
        return redisTemplate.opsForSet().members(sessionKey);
    }
    
    /**
     * 检查用户是否在指定会话中
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 是否在会话中
     */
    public boolean isUserInSession(Long userId, Long sessionId) {
        String sessionKey = RedisKey.getCallSessionMembersKey(sessionId);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(sessionKey, userId));
    }
    
    /**
     * 结束会话，清理所有相关缓存
     * @param sessionId 会话ID
     */
    public void endSession(Long sessionId) {
        String sessionKey = RedisKey.getCallSessionMembersKey(sessionId);
        Set<Object> userIds = redisTemplate.opsForSet().members(sessionKey);
        
        if (userIds != null) {
            for (Object userId : userIds) {
                String userKey = RedisKey.getUserCallSessionKey((Long) userId);
                redisTemplate.delete(userKey);
            }
        }
        
        redisTemplate.delete(sessionKey);
        log.info("会话{}已结束，相关缓存已清理", sessionId);
    }
}