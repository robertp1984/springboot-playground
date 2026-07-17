package org.softwarecave.springbootnote.outbox.service.dispatch;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.softwarecave.springbootnote.outbox.model.AggregateType;
import org.softwarecave.springbootnote.outbox.model.MessageType;
import org.softwarecave.springbootnote.outbox.model.Outbox;
import org.softwarecave.springbootnote.outbox.model.Status;
import org.softwarecave.springbootnote.outbox.service.InvalidOutboxDataException;
import org.softwarecave.springbootnote.outbox.service.OutboxRepository;
import org.springframework.data.domain.PageImpl;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxDispatcherTest {

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private OutboxDispatcherStrategy jsonStrategy;

    @Mock
    private OutboxDispatcherStrategy avroStrategy;

    @InjectMocks
    private OutboxDispatcher outboxDispatcher;

    @BeforeEach
    void setUp() {
        // Marked as lenient because there is no point repeating parts of this code in every test method
        lenient().when(jsonStrategy.getMessageType()).thenReturn(MessageType.JSON);
        lenient().when(avroStrategy.getMessageType()).thenReturn(MessageType.AVRO);
    }

    @Test
    void testSetDispatcherStrategies_ConflictingMessageTypes() {
        var conflictingStrategy = mock(OutboxDispatcherStrategy.class);
        when(conflictingStrategy.getMessageType()).thenReturn(MessageType.JSON);

        assertThatThrownBy(() -> outboxDispatcher.setDispatcherStrategies(List.of(jsonStrategy, conflictingStrategy)))
                .isInstanceOf(InvalidOutboxDataException.class)
                .hasMessageContaining("conflicting Outbox dispatchers strategies");
    }

    @Test
    void testProcessWithNewOutboxEntries() throws Exception {
        // given
        outboxDispatcher.setDispatcherStrategies(List.of(jsonStrategy, avroStrategy));

        var outbox1 = createOutbox(1L, MessageType.JSON);
        var outbox2 = createOutbox(2L, MessageType.AVRO);

        when(outboxRepository.findByStatus(eq(Status.NEW), any()))
                .thenReturn(new PageImpl<Outbox>(List.of(outbox1, outbox2)));

        var future1 = CompletableFuture.completedFuture(mock(RecordMetadata.class));
        var future2 = CompletableFuture.completedFuture(mock(RecordMetadata.class));

        when(jsonStrategy.send(outbox1)).thenReturn(future1);
        when(avroStrategy.send(outbox2)).thenReturn(future2);

        // when
        outboxDispatcher.process();

        // then
        var captor = ArgumentCaptor.forClass(Outbox.class);
        verify(outboxRepository, times(2)).save(captor.capture());

        List<Outbox> savedOutboxes = captor.getAllValues();
        assertThat(savedOutboxes).hasSize(2);
        assertThat(savedOutboxes).allMatch(outbox -> outbox.getStatus() == Status.SENT);
    }

    @Test
    void testProcessWithEmptyOutboxEntries() {
        // given
        outboxDispatcher.setDispatcherStrategies(List.of(jsonStrategy, avroStrategy));

        when(outboxRepository.findByStatus(eq(Status.NEW), any()))
                .thenReturn(new PageImpl<Outbox>(List.of()));

        // when
        outboxDispatcher.process();

        // then
        verify(outboxRepository, never()).save(any());
    }

    @Test
    void testProcessWithKafkaFailure() throws Exception {
        // given
        outboxDispatcher.setDispatcherStrategies(List.of(jsonStrategy));

        var outbox = createOutbox(1L, MessageType.JSON);

        when(outboxRepository.findByStatus(eq(Status.NEW), any()))
                .thenReturn(new PageImpl<Outbox>(List.of(outbox)));

        var future = CompletableFuture.<RecordMetadata>failedFuture(new RuntimeException("Kafka error"));
        when(jsonStrategy.send(outbox)).thenReturn(future);

        // when
        outboxDispatcher.process();

        // then
        verify(outboxRepository, never()).save(any());
    }

    @Test
    void testSendToKafkaWithUnrecognizedMessageType() {
        outboxDispatcher.setDispatcherStrategies(List.of(jsonStrategy));

        var outbox = createOutbox(1L, MessageType.AVRO);

        assertThatThrownBy(() -> {
            when(outboxRepository.findByStatus(eq(Status.NEW), any()))
                    .thenReturn(new PageImpl<Outbox>(List.of(outbox)));
            outboxDispatcher.process();
        }).isInstanceOf(InvalidOutboxDataException.class)
                .hasMessageContaining("Unrecognized message type");
    }

    @Test
    void testProcessMultipleEntriesWithMixedResults() throws Exception {
        // given
        outboxDispatcher.setDispatcherStrategies(List.of(jsonStrategy, avroStrategy));

        var outbox1 = createOutbox(1L, MessageType.JSON);
        var outbox2 = createOutbox(2L, MessageType.JSON);

        when(outboxRepository.findByStatus(eq(Status.NEW), any()))
                .thenReturn(new PageImpl<Outbox>(List.of(outbox1, outbox2)));

        var future1 = CompletableFuture.completedFuture(mock(RecordMetadata.class));
        when(jsonStrategy.send(outbox1)).thenReturn(future1);

        var future2 = CompletableFuture.<RecordMetadata>failedFuture(new RuntimeException("Kafka error"));
        when(jsonStrategy.send(outbox2)).thenReturn(future2);

        // when
        outboxDispatcher.process();

        // then
        var captor = ArgumentCaptor.forClass(Outbox.class);
        verify(outboxRepository).save(captor.capture());

        List<Outbox> savedOutboxes = captor.getAllValues();
        assertThat(savedOutboxes).hasSize(1);
        assertThat(savedOutboxes.getFirst().getId()).isEqualTo(1L);
        assertThat(savedOutboxes.getFirst().getStatus()).isEqualTo(Status.SENT);
    }

    private Outbox createOutbox(Long id, MessageType messageType) {
        var outbox = new Outbox();
        outbox.setId(id);
        outbox.setAggregateType(AggregateType.STICKY_NOTE);
        outbox.setAggregateId(100L);
        outbox.setMessageType(messageType);
        outbox.setStatus(Status.NEW);
        outbox.setCreatedDate(ZonedDateTime.now());
        outbox.setPayloadString("{\"test\": \"data\"}");
        return outbox;
    }
}
