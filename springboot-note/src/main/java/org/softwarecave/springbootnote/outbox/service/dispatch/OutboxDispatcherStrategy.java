package org.softwarecave.springbootnote.outbox.service.dispatch;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.softwarecave.springbootnote.outbox.model.MessageType;
import org.softwarecave.springbootnote.outbox.model.Outbox;

import java.util.concurrent.Future;

public interface OutboxDispatcherStrategy {
    Future<RecordMetadata> send(Outbox outbox);

    MessageType getMessageType();
}
