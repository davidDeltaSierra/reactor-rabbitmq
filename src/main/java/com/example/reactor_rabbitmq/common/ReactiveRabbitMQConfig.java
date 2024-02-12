package com.example.reactor_rabbitmq.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.rabbitmq.*;

import java.util.function.Supplier;

@Slf4j
@Configuration
class ReactiveRabbitMQConfig {
    @Bean
    ConnectionFactory connectionFactory(@Value("${spring.rabbitmq.host}") String host) {
        var connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.useNio();
        return connectionFactory;
    }

    @Bean
    Receiver receiver(ConnectionFactory connectionFactory) {
        return RabbitFlux.createReceiver(
                new ReceiverOptions()
                        .connectionFactory(connectionFactory)
        );
    }

    @Bean
    Sender sender(ConnectionFactory connectionFactory) {
        return RabbitFlux.createSender(
                new SenderOptions()
                        .connectionFactory(connectionFactory)
        );
    }

    @Bean
    ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }
}
