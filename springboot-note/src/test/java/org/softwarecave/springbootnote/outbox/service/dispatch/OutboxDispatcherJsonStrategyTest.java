package org.softwarecave.springbootnote.outbox.service.dispatch;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.softwarecave.springbootnote.outbox.kafka.KafkaJsonProducer;
import org.softwarecave.springbootnote.outbox.model.AggregateType;
import org.softwarecave.springbootnote.outbox.model.MessageType;
import org.softwarecave.springbootnote.outbox.model.Outbox;
import org.softwarecave.springbootnote.outbox.model.Status;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxDispatcherJsonStrategyTest {

    @Mock
    private KafkaJsonProducer kafkaJsonProducer;

    @InjectMocks
    private OutboxDispatcherJsonStrategy strategy;

    @Test
    void testGetMessageType() {
        assertThat(strategy.getMessageType()).isEqualTo(MessageType.JSON);
    }

    @Test
    void testSendCallsKafkaJsonProducer() {
        Outbox outbox = createOutbox();

        Future<RecordMetadata> mockFuture = CompletableFuture.completedFuture(mock(RecordMetadata.class));
        when(kafkaJsonProducer.sendToKafka(any(Outbox.class))).thenReturn(mockFuture);

        strategy.send(outbox);

        verify(kafkaJsonProducer).sendToKafka(any(Outbox.class));
    }

    @Test
    void testMultipleSendOperations() {
        Outbox outbox1 = createOutbox();
        Outbox outbox2 = createOutbox();
        outbox2.setId(2L);

        Future<RecordMetadata> mockFuture1 = CompletableFuture.completedFuture(mock(RecordMetadata.class));
        Future<RecordMetadata> mockFuture2 = CompletableFuture.completedFuture(mock(RecordMetadata.class));

        when(kafkaJsonProducer.sendToKafka(outbox1)).thenReturn(mockFuture1);
        when(kafkaJsonProducer.sendToKafka(outbox2)).thenReturn(mockFuture2);

        Future<RecordMetadata> result1 = strategy.send(outbox1);
        Future<RecordMetadata> result2 = strategy.send(outbox2);

        assertThat(result1).isEqualTo(mockFuture1);
        assertThat(result2).isEqualTo(mockFuture2);
        verify(kafkaJsonProducer).sendToKafka(outbox1);
        verify(kafkaJsonProducer).sendToKafka(outbox2);
    }


    @Test
    void testSendWithDifferentPayloads() {
        Outbox outboxWithJsonPayload = createOutbox();
        outboxWithJsonPayload.setPayloadString("{\"key\": \"value\"}");

        Outbox outboxWithComplexPayload = createOutbox();
        outboxWithComplexPayload.setPayloadString("{\"nested\": {\"data\": [1, 2, 3]}}");

        Future<RecordMetadata> mockFuture = CompletableFuture.completedFuture(mock(RecordMetadata.class));
        when(kafkaJsonProducer.sendToKafka(any(Outbox.class))).thenReturn(mockFuture);

        strategy.send(outboxWithJsonPayload);
        strategy.send(outboxWithComplexPayload);

        verify(kafkaJsonProducer).sendToKafka(outboxWithJsonPayload);
        verify(kafkaJsonProducer).sendToKafka(outboxWithComplexPayload);
    }

    private Outbox createOutbox() {
        var outbox = new Outbox();
        outbox.setId(1L);
        outbox.setAggregateType(AggregateType.STICKY_NOTE);
        outbox.setAggregateId(100L);
        outbox.setMessageType(MessageType.JSON);
        outbox.setStatus(Status.NEW);
        outbox.setCreatedDate(ZonedDateTime.now());
        outbox.setPayloadString("{\"test\": \"data\"}");
        return outbox;
    }
}
