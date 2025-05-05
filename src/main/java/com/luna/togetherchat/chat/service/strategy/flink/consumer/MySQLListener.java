package com.luna.togetherchat.chat.service.strategy.flink.consumer;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cdc.connectors.mysql.source.MySqlSource;
import org.apache.flink.cdc.connectors.mysql.table.StartupOptions;
import org.apache.flink.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MySQLListener implements ApplicationRunner {

    @Value("${tuanchat.mysql.ip}")
    private  String mysqlIP;
    @Value("${tuanchat.mysql.port}")
    private  String mysqlPort;
    @Value("${tuanchat.mysql.username}")
    private  String mysqlUsername;
    @Value("${tuanchat.mysql.password}")
    private  String mysqlPassword;
    @Value("${tuanchat.mysql.db}")
    private  String mysqlDb;
    @Value("${tuanchat.redis.host}")
    private  String redisHost;
    @Value("${tuanchat.redis.port}")
    private  String redisPort;
    @Value("${tuanchat.message.table.name}")
    private  String message;
    @Value("${tuanchat.room_member.table.name}")
    private  String room_group_member;


    private final MQSink mqSink;
    public MySQLListener(MQSink mqSink) {
        this.mqSink = mqSink;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        MySqlSource<String> source = getStringMySqlSource();

        // 启动webUI
        org.apache.flink.configuration.Configuration configuration = new org.apache.flink.configuration.Configuration();
        configuration.setInteger(RestOptions.PORT, 8082);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(configuration);
        env.setParallelism(1); // debug 用途，非生产环境使用

        // checkpoint 的间隔时间，侧重点是容错，如果flink作业意外失败兵重启之后，从早先打下的checkpoint回复运行
        env.enableCheckpointing(5000);

        DataStreamSink sink = env.fromSource(
                        source,
                        WatermarkStrategy.noWatermarks(),
                        "tuanchat-mysql")
                .addSink(mqSink);
//        env.execute("TuanChat Application");
    }

    private MySqlSource<String> getStringMySqlSource() {
        MySqlSource<String> source = MySqlSource.<String> builder()
                .hostname(mysqlIP)
                .port(Integer.parseInt(mysqlPort))
                .username(mysqlUsername)
                .password(mysqlPassword)
                .databaseList(mysqlDb) // monitor all tables under "mydb" database
                .tableList(mysqlDb + "." + message, mysqlDb + "." + room_group_member)
                .startupOptions(StartupOptions.latest())
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to String
                .build();
        return source;
    }
}
