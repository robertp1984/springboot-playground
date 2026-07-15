package org.softwarecave.springbootnote.outbox.service.dispatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.softwarecave.springbootnote.avro.StickyNote;
import org.softwarecave.springbootnote.outbox.kafka.KafkaAvroProducer;
import org.softwarecave.springbootnote.outbox.model.AggregateType;
import org.softwarecave.springbootnote.outbox.model.MessageType;
import org.softwarecave.springbootnote.outbox.model.Outbox;
import org.softwarecave.springbootnote.outbox.service.InvalidOutboxDataException;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@RequiredArgsConstructor
@Service
@Slf4j
public class OutboxDispatcherAvroStrategy implements OutboxDispatcherStrategy {
    private final KafkaAvroProducer kafkaAvroProducer;

    @Override
    public Future<RecordMetadata> send(Outbox outbox) {
        return kafkaAvroProducer.sendToKafka(outbox, getAvroClass(outbox));
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.AVRO;
    }

    private Class<? extends SpecificRecord> getAvroClass(Outbox entry) {
        AggregateType aggregateType = entry.getAggregateType();
        return switch (aggregateType) {
            case STICKY_NOTE -> StickyNote.class; // TODO: encapsulate this
            case null -> throw new InvalidOutboxDataException("Unrecognized aggregate type " + aggregateType);
        };
    }
}
