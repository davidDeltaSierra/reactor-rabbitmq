package com.example.reactor_rabbitmq.common;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import reactor.core.Disposable;
import reactor.rabbitmq.Sender;

import static reactor.rabbitmq.QueueSpecification.queue;

@Configuration
@RequiredArgsConstructor
class ReactiveRabbitMQTopology {
    private final Sender sender;

    @PostConstruct
    Disposable init() {
        return sender.declareQueue(queue("big-queue").durable(true))
                .subscribe();
    }
}
