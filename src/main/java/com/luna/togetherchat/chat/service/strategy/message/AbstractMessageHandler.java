package com.luna.togetherchat.chat.service.strategy.message;

import cn.hutool.core.bean.BeanUtil;
import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.chat.domain.entity.message.MessageExtra;
import com.luna.togetherchat.chat.domain.request.message.ChatMessageRequest;
import com.luna.togetherchat.chat.enums.MessageTypeEnum;
import com.luna.togetherchat.common.utils.AssertUtil;
import jakarta.annotation.PostConstruct;

import java.lang.reflect.ParameterizedType;

/**
 * 消息处理器抽象类
 *
 */
public abstract class AbstractMessageHandler<Req> {

    /* ============ 工具类，用于 转换出数据类型 以及 索引处理类 ============ */

    private Class<Req> bodyClass;

    abstract MessageTypeEnum getMessageTypeEnum();

    // 使得 enum 可以 索引到对应的处理类
    @PostConstruct
    private void init() {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.bodyClass = (Class<Req>) genericSuperclass.getActualTypeArguments()[0];
        MessageHandlerFactory.register(getMessageTypeEnum().getType(), this);
    }

    private Req toBean(Object body) {
        if (bodyClass.isAssignableFrom(body.getClass())) {return (Req) body;}

        return BeanUtil.toBean(body, bodyClass);
    }


    /**
     * 验证并转换消息
     *
     * @param request 请求体
     * @return Message 验证后，有效的Message
     */
    public Message checkAndConvert(ChatMessageRequest request, Long userId) {
        Req body = this.toBean(request.getBody());
        AssertUtil.allCheckValidateThrow(body);

        Message insert = new Message(request, userId);
        //插入文本内容
        MessageExtra extra = new MessageExtra(body);
        insert.setExtra(extra);
        return insert;
    }
}
