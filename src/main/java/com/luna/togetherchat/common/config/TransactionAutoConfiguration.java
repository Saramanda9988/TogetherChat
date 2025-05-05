package com.luna.togetherchat.common.config;


import com.jxc.tuanchat.chat.event.MQProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-06
 */
@Configuration
@EnableScheduling
public class TransactionAutoConfiguration {

    @Nullable
    protected Executor executor;

    @Bean
    public MQProducer getMQProducer() {
        return new MQProducer();
    }
}
