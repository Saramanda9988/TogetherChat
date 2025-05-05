package com.luna.togetherchat.chat.service.strategy.flink.sink.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Change {
    /** 变更前数据 */
    private String beforeData;
    /** 变更后数据 */
    private String afterData;
    /** 变更类型 1新增 2修改 3删除 */
    private ChangeTypeEnum operationType;
    /** 数据库名 */
    private String databaseName;
    /** 表名 */
    private String tableName;
}
