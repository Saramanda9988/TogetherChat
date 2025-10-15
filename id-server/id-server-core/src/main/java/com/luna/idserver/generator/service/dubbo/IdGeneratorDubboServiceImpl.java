package com.luna.idserver.generator.service.dubbo;

import com.luna.idserver.api.service.IdGenerator;
import com.luna.idserver.generator.service.IdCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
@AllArgsConstructor
@DubboService(interfaceClass = IdGenerator.class) //TODO: 或许不需要进行分组
@Service
public class IdGeneratorDubboServiceImpl implements IdGenerator {
    private final IdCache idCache;

    /**
     * 生成消息ID
     */
    @Override
    public Long generateMessageId() throws RuntimeException {
        return idCache.getNextMessageId();
    }

    /**
     * 生成对话同步ID
     *
     * @param conversationId
     */
    @Override
    public Long generateConversationSyncId(Long conversationId) throws RuntimeException {
        return idCache.getNextSyncId(conversationId);
    }

    /**
     * 生成雪花id
     */
    @Override
    public String generateSnowflakeId() {
        // TODO: 实现主键使用雪花算法生成
        return "";
    }
}
