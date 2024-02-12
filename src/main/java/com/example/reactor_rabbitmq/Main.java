package com.example.reactor_rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Main {
    private final Receiver receiver;
    private final Sender sender;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "{batchSize}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Mono<?> init(@PathVariable Integer batchSize) {
        return sender.send(Flux.range(1, batchSize)
                .map(it -> new OutboundMessage("", "big-queue", newPayload(it.toString()))));
    }

    @PostConstruct
    Disposable initConsumers() {
        return receiver.consumeManualAck("big-queue").subscribe(acknowledgableDelivery -> {
            var payload = mapper(acknowledgableDelivery.getBody());
            acknowledgableDelivery.ack();
        });
    }

    @SneakyThrows
    private byte[] newPayload(String id) {
        return objectMapper.writeValueAsBytes(new Payload(id));
    }

    @SneakyThrows
    private Payload mapper(byte[] data) {
        return objectMapper.readValue(data, Payload.class);
    }

    public record Payload(String id) {

    }
}
