package com.luna.chatserver.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                              MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        // 禁用Spring的重试机制，改用RabbitMQ自带的重试
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("dead.letter.queue").build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange("dead.letter.exchange").build();
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("dead.letter");
    }

    @Bean
    public Queue messageQueue() {
        return QueueBuilder.durable("message.queue")
                .withArgument("x-dead-letter-exchange", "dead.letter.exchange")
                .withArgument("x-dead-letter-routing-key", "dead.letter")
                .withArgument("x-message-ttl", 10000) // 消息存活时间，单位毫秒
                .withArgument("x-max-retries", 3) // 最大重试次数
                .build();
    }

    @Bean
    public TopicExchange messageExchange() {
        return ExchangeBuilder.topicExchange("message.exchange").durable(true).build();
    }

    @Bean
    public Binding messageBinding() {
        return BindingBuilder.bind(messageQueue())
                .to(messageExchange())
                .with("message.#");
    }
}
