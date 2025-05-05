package com.luna.togetherchat.chat.service.strategy.flink.sink.entity;

import java.io.Serializable;

/** 变更类型 1新增 2修改 3删除 */
public enum ChangeTypeEnum implements Serializable {
    INSERT(),
    UPDATE(),
    DELETE();
}
