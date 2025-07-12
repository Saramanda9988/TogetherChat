package com.luna.chatserver.common.utils;

import com.luna.chatserver.common.constant.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工具类
 * 基于Redis实现的分布式锁
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockUtils {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final long DEFAULT_LOCK_TIMEOUT = 10; // 默认锁超时时间（秒）
    
    /**
     * 获取分布式锁
     * @param lockKey 锁的键
     * @param requestId 请求标识
     * @param timeout 超时时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, long timeout) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, timeout, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("获取分布式锁异常: {}", lockKey, e);
            return false;
        }
    }
    
    /**
     * 获取分布式锁（使用默认超时时间）
     * @param lockKey 锁的键
     * @param requestId 请求标识
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId) {
        return tryLock(lockKey, requestId, DEFAULT_LOCK_TIMEOUT);
    }
    
    /**
     * 获取通话会话锁
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 锁信息（包含锁键和请求ID）
     */
    public LockInfo tryLockCallSession(Long sessionId, Long userId) {
        String lockKey = RedisKey.getCallSessionLockKey(sessionId, userId);
        String requestId = UUID.randomUUID().toString();
        boolean locked = tryLock(lockKey, requestId);
        return new LockInfo(lockKey, requestId, locked);
    }
    
    /**
     * 释放分布式锁
     * @param lockKey 锁的键
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        try {
            Object currentValue = redisTemplate.opsForValue().get(lockKey);
            if (currentValue != null && currentValue.equals(requestId)) {
                return Boolean.TRUE.equals(redisTemplate.delete(lockKey));
            }
            return false;
        } catch (Exception e) {
            log.error("释放分布式锁异常: {}", lockKey, e);
            return false;
        }
    }
    
    /**
     * 释放锁信息
     * @param lockInfo 锁信息
     * @return 是否释放成功
     */
    public boolean releaseLock(LockInfo lockInfo) {
        if (lockInfo != null && lockInfo.isLocked()) {
            return releaseLock(lockInfo.getLockKey(), lockInfo.getRequestId());
        }
        return false;
    }
    
    /**
     * 锁信息类
     */
    public static class LockInfo {
        private final String lockKey;
        private final String requestId;
        private final boolean locked;
        
        public LockInfo(String lockKey, String requestId, boolean locked) {
            this.lockKey = lockKey;
            this.requestId = requestId;
            this.locked = locked;
        }
        
        public String getLockKey() {
            return lockKey;
        }
        
        public String getRequestId() {
            return requestId;
        }
        
        public boolean isLocked() {
            return locked;
        }
    }
}