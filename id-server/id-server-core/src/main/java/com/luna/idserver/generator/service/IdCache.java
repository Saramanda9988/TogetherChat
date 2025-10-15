package com.luna.idserver.generator.service;

import com.luna.common.utils.RedisUtils;
import com.luna.messageserver.api.service.MessageDubboService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>
 *  id缓存器
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
@Component
@RequiredArgsConstructor
public class IdCache {

    @DubboReference(group = "message-service")
    private MessageDubboService messageDubboService;

    @PostConstruct
    private void init() {
        Long maxMessageId = messageDubboService.getMaxMessageId();
        Map<Long, Integer> maxSyncIds = messageDubboService.getMaxSyncIds();
        for (Map.Entry<Long, Integer> entry : maxSyncIds.entrySet()) {
            RedisUtils.set("conversationId:" + entry.getKey(), entry.getValue());
        }
        RedisUtils.set("messageId:", maxMessageId);
    }

    public Long getNextSyncId(Long conversationId) throws RuntimeException{
        Long inc = RedisUtils.inc("conversationId:" + conversationId);
        if (inc == -1L) {
            throw new RuntimeException("获取下一个syncId失败，conversationId不存在: " + conversationId);
        }
        return inc;
    }

    public Long getNextMessageId() throws RuntimeException{
        Long inc = RedisUtils.inc("messageId:");
        if (inc == -1L) {
            throw new RuntimeException("获取下一个messageId失败, 请稍后重试");
        }
        return inc;
    }
}
