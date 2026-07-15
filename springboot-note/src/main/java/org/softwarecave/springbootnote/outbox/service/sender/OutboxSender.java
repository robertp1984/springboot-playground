package org.softwarecave.springbootnote.outbox.service.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.softwarecave.springbootnote.avro.StickyNote;
import org.softwarecave.springbootnote.outbox.kafka.KafkaAvroProducer;
import org.softwarecave.springbootnote.outbox.kafka.KafkaJsonProducer;
import org.softwarecave.springbootnote.outbox.model.AggregateType;
import org.softwarecave.springbootnote.outbox.model.MessageType;
import org.softwarecave.springbootnote.outbox.model.Outbox;
import org.softwarecave.springbootnote.outbox.model.Status;
import org.softwarecave.springbootnote.outbox.service.InvalidOutboxDataException;
import org.softwarecave.springbootnote.outbox.service.OutboxRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxSender {

    private final OutboxRepository outboxRepository;
    private final KafkaJsonProducer kafkaJsonProducer;
    private final KafkaAvroProducer kafkaAvroProducer;

    @Scheduled(fixedDelayString = "${app.outbox.sender.delay}", timeUnit = TimeUnit.MILLISECONDS)
    public void process() {
        var entryList = outboxRepository.findByStatus(Status.NEW,
                PageRequest.of(0, 100, Sort.by(Sort.Order.asc("createdDate"))));
        log.info("Fetched {} entries from outbox to process", entryList.getContent().size());

        var futureList = sendToKafka(entryList);

        waitForKafkaAcks(futureList, entryList);

    }

    private void waitForKafkaAcks(ArrayList<Future<RecordMetadata>> futureList, Page<Outbox> entryList) {
        for (int i = 0; i < futureList.size(); i++) {
            var future = futureList.get(i);
            try {
                RecordMetadata recordMetadata = future.get();
                var entry = entryList.getContent().get(i);

                updateStatusAsSent(entry);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Failed sending the message from outbox ", e);
            }
        }
    }

    private void updateStatusAsSent(Outbox entry) {
        log.info("Set the status of outbox entry {} to SENT", entry.getPayloadString());
        entry.setStatus(Status.SENT);
        outboxRepository.save(entry);
    }

    private ArrayList<Future<RecordMetadata>> sendToKafka(Page<Outbox> entryList) {
        var futureList = new ArrayList<Future<RecordMetadata>>();
        for (var entry : entryList) {
            futureList.add(sendToKafka(entry));
        }
        return futureList;
    }

    private Future<RecordMetadata> sendToKafka(Outbox entry) {
        MessageType messageType = entry.getMessageType();
        return switch (messageType) {
            case AVRO -> kafkaAvroProducer.sendToKafka(entry, getAvroClass(entry));
            case JSON -> kafkaJsonProducer.sendToKafka(entry);
            case null -> throw new InvalidOutboxDataException("Unrecognized message type " + messageType);
        };
    }

    private Class<? extends SpecificRecord> getAvroClass(Outbox entry) {
        AggregateType aggregateType = entry.getAggregateType();
        return switch (aggregateType) {
            case STICKY_NOTE -> StickyNote.class;
            case null -> throw new InvalidOutboxDataException("Unrecognized aggregate type " + aggregateType);
        };
    }
}
