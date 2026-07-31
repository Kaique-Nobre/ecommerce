package com.ecommerce.menssaging.config;

import com.ecommerce.menssaging.constants.RabbitConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange ecommerceExchange() {
        return new TopicExchange(
                RabbitConstants.ECOMMERCE_EXCHANGE
        );
    }

    @Bean
    public Queue userCreatedQueue() {
        return QueueBuilder
                .durable(RabbitConstants.USER_CREATED_QUEUE)
                .deadLetterExchange(RabbitConstants.DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(RabbitConstants.USER_CREATED_QUEUE)
                .build();
    }

    @Bean
    public Queue userCreatedDeadLetterQueue() {
        return QueueBuilder
                .durable(RabbitConstants.USER_CREATED_DLQ)
                .build();

    }

    @Bean
    public Binding userCreatedBrinding(
            Queue userCreatedQueue,
            TopicExchange ecommerceExchange
    ) {
        return BindingBuilder
                .bind(userCreatedQueue)
                .to(ecommerceExchange)
                .with(RabbitConstants.USER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding deadLetterBinding(
            Queue userCreatedDeadLetterQueue,
            TopicExchange deadLetterExchange
    ) {
        return BindingBuilder
                .bind(userCreatedDeadLetterQueue)
                .to(deadLetterExchange)
                .with(RabbitConstants.USER_CREATED_QUEUE);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter converter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);

        return factory;
    }

    @Bean
    public RetryTemplate retryTemplate() {

        RetryTemplate retryTemplate = new RetryTemplate();

        return retryTemplate;
    }

}
