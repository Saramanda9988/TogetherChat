package com.luna.togetherchat.chat.service.strategy.flink.consumer;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luna.togetherchat.chat.service.strategy.flink.sink.entity.Change;
import com.luna.togetherchat.chat.service.strategy.flink.sink.entity.ChangeTypeEnum;
import com.luna.togetherchat.common.constant.MQConstant;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MQSink extends RichSinkFunction<String> {

    protected Connection connection;
    protected Channel channel;

    @Value("${tuanchat.rabbitmq.port}")
    private Integer mqPort;
    @Value("${tuanchat.rabbitmq.host}")
    private String mqHost;
    @Value("${tuanchat.rabbitmq.username}")
    private String mqUsername;
    @Value("${tuanchat.rabbitmq.password}")
    private String mqPassword;

    @Override
    public void invoke(String json, Context context) throws Exception {
        JSONObject jsonObject = new JSONObject(json);
        // 构造Change对象，数据，表名，数据库名
        Change change = Change.builder()
                .beforeData(String.valueOf(jsonObject.get("before")))
                .afterData(String.valueOf(jsonObject.get("after")))
                .tableName(String.valueOf(((JSONObject) jsonObject.get("source")).get("table")))
                .databaseName(String.valueOf(((JSONObject) jsonObject.get("source")).get("db")))
                .build();
        // 解析操作类型
        switch ((String) jsonObject.get("op")) {
            case "c" -> change.setOperationType(ChangeTypeEnum.INSERT);
            case "u" -> change.setOperationType(ChangeTypeEnum.UPDATE);
            case "d" -> change.setOperationType(ChangeTypeEnum.DELETE);
            default -> throw new IllegalStateException("Unexpected value: " + jsonObject.get("op"));
        }
        // 用jackson序列化Change对象，并发送消息到RabbitMQ
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(change);
        channel.basicPublish(MQConstant.MYSQL_CHANGE_EXCHANGE, "db.change", null, s.getBytes());
    }

    /**
     * 在启动SpringBoot项目是加载了Spring容器，其他地方可以使用@Autowired获取Spring容器中的类；但是Flink启动的项目中，
     * 默认启动了多线程执行相关代码，导致在其他线程无法获取Spring容器，只有在Spring所在的线程才能使用@Autowired，
     * 故在Flink自定义的Sink的open()方法中初始化Spring容器
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        // 设置RabbitMQ相关信息
        factory.setHost(mqHost);
        factory.setUsername(mqUsername);
        factory.setPassword(mqPassword);
        factory.setPort(mqPort);

        // 创建一个通道
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(MQConstant.MYSQL_CHANGE_QUEUE, true, false, false, null);

        // 声明交换机
        channel.exchangeDeclare(MQConstant.MYSQL_CHANGE_EXCHANGE, BuiltinExchangeType.DIRECT, true);
    }
}



