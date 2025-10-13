package com.luna.idserver.generator.service.dubbo;

import com.luna.idserver.api.service.IdGenerator;
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
    /**
     * 生成群聊消息ID
     */
    @Override
    public Long generateGroupMessageId() {
        return 0L;
    }

    /**
     * 生成私聊消息ID
     */
    @Override
    public Long generateP2PMessageId() {
        return 0L;
    }

    /**
     * 生成对话同步ID
     *
     * @param groupId
     */
    @Override
    public Long generateRoomSyncId(Long groupId) {
        return 0L;
    }

    /**
     * 生成雪花id
     */
    @Override
    public String generateSnowflakeId() {
        return "";
    }
}
