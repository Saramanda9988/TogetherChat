package com.luna.togetherchat.chat.service.strategy.flink.sink;

import cn.hutool.core.bean.BeanUtil;
import com.luna.togetherchat.chat.service.strategy.flink.SinkFactory;
import com.luna.togetherchat.chat.service.strategy.flink.sink.entity.Change;
import jakarta.annotation.PostConstruct;

import java.lang.reflect.ParameterizedType;

/**
 * Description: 消息处理器抽象类
 */
public abstract class AbstractSink<Req> {

    private Class<Req> bodyClass;

    @PostConstruct
    private void init() {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.bodyClass = (Class<Req>) genericSuperclass.getActualTypeArguments()[0];
        SinkFactory.register(getTableName(), this);
    }

    public abstract String getTableName();

    private Req toBean(Object body) {
        if (bodyClass.isAssignableFrom(body.getClass())) { return (Req) body; }
        return BeanUtil.toBean(body, bodyClass);
    }

    public void process(Change change) {
        switch (change.getOperationType()){
            case INSERT -> {
                Req after = toBean(change.getAfterData());
                processInsert(change,after);
            }
            case UPDATE -> {
                Req before = toBean(change.getAfterData());
                processUpdate(change,before);
            }
            case DELETE -> {
                Req before = toBean(change.getAfterData());
                processDelete(change, before);
            }
            default -> throw new IllegalStateException("Unexpected value: " + change.getOperationType());
        }
    }

    public void processInsert(Change change, Req after) {}
    public void processUpdate(Change change, Req before) {}
    public void processDelete(Change change, Req before) {}
}
