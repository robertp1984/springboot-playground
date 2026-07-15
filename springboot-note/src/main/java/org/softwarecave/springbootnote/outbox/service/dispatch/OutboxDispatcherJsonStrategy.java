package org.softwarecave.springbootnote.outbox.service.dispatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.softwarecave.springbootnote.outbox.kafka.KafkaJsonProducer;
import org.softwarecave.springbootnote.outbox.model.MessageType;
import org.softwarecave.springbootnote.outbox.model.Outbox;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@RequiredArgsConstructor
@Service
@Slf4j
public class OutboxDispatcherJsonStrategy implements OutboxDispatcherStrategy {
    private final KafkaJsonProducer kafkaJsonProducer;

    @Override
    public Future<RecordMetadata> send(Outbox outbox) {
        return kafkaJsonProducer.sendToKafka(outbox);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.JSON;
    }
}
