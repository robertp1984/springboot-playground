package org.softwarecave.springbootnote.outbox.service.dispatch;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.softwarecave.springbootnote.avro.StickyNote;
import org.softwarecave.springbootnote.outbox.kafka.KafkaAvroProducer;
import org.softwarecave.springbootnote.outbox.model.AggregateType;
import org.softwarecave.springbootnote.outbox.model.MessageType;
import org.softwarecave.springbootnote.outbox.model.Outbox;
import org.softwarecave.springbootnote.outbox.model.Status;
import org.softwarecave.springbootnote.outbox.service.InvalidOutboxDataException;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxDispatcherAvroStrategyTest {

    @Mock
    private KafkaAvroProducer kafkaAvroProducer;

    @InjectMocks
    private OutboxDispatcherAvroStrategy strategy;

    @Test
    void testGetMessageType() {
        assertThat(strategy.getMessageType()).isEqualTo(MessageType.AVRO);
    }

    @Test
    void testSendWithValidStickyNoteOutbox() {
        Outbox outbox = createOutboxWithStickyNote();
        Future<RecordMetadata> mockFuture = CompletableFuture.completedFuture(mock(RecordMetadata.class));

        when(kafkaAvroProducer.sendToKafka(eq(outbox), eq(StickyNote.class)))
                .thenReturn(mockFuture);

        Future<RecordMetadata> result = strategy.send(outbox);

        assertThat(result).isEqualTo(mockFuture);
        verify(kafkaAvroProducer).sendToKafka(outbox, StickyNote.class);
    }

    @Test
    void testSendWithNullAggregateType() {
        Outbox outbox = new Outbox(1L, AggregateType.STICKY_NOTE, 100L, MessageType.AVRO, ZonedDateTime.now(), new byte[]{1, 2, 3}, null, Status.NEW);
        outbox.setAggregateType(null);

        assertThatThrownBy(() -> strategy.send(outbox))
                .isInstanceOf(InvalidOutboxDataException.class)
                .hasMessageContaining("Unrecognized aggregate type");
    }

    @Test
    void testSendWithDifferentPayloadBytes() {
        Outbox outbox1 = createOutboxWithStickyNote();
        outbox1.setPayloadBytes(new byte[]{1, 2, 3, 4, 5});

        Outbox outbox2 = createOutboxWithStickyNote();
        outbox2.setId(2L);
        outbox2.setPayloadBytes(new byte[]{10, 20, 30});

        Future<RecordMetadata> mockFuture = CompletableFuture.completedFuture(mock(RecordMetadata.class));
        when(kafkaAvroProducer.sendToKafka(any(Outbox.class), eq(StickyNote.class)))
                .thenReturn(mockFuture);

        strategy.send(outbox1);
        strategy.send(outbox2);

        verify(kafkaAvroProducer).sendToKafka(outbox1, StickyNote.class);
        verify(kafkaAvroProducer).sendToKafka(outbox2, StickyNote.class);
    }

    private Outbox createOutboxWithStickyNote() {
        return new Outbox(1L, AggregateType.STICKY_NOTE, 100L,
                MessageType.AVRO, ZonedDateTime.now(),
                new byte[]{1, 2, 3}, null, Status.NEW);
    }
}
