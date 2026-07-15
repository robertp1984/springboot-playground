package org.softwarecave.springbootnote.outbox.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.outbox.kafka.converter.AvroStickyNoteConverter;
import org.softwarecave.springbootnote.outbox.model.AggregateType;
import org.softwarecave.springbootnote.outbox.model.MessageType;
import org.softwarecave.springbootnote.outbox.model.Outbox;
import org.softwarecave.springbootnote.outbox.model.Status;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBooleanProperty(prefix = "app.kafka.avro", name = "enabled", havingValue = true)
public class AvroOutboxService implements OutboxService {

    private final OutboxRepository outboxRepository;
    private final AvroStickyNoteConverter avroStickyNoteConverter;

    @Transactional
    @Override
    public void send(StickyNote stickyNote) {
        ByteBuffer payload = avroStickyNoteConverter.convertToByteBuffer(stickyNote);
        byte[] payloadBinary = payload.array();

        Outbox outbox = new Outbox(null, AggregateType.STICKY_NOTE, stickyNote.getId(),
                MessageType.AVRO, ZonedDateTime.now(), payloadBinary, null, Status.NEW);

        log.info("Saving to outbox: {}", outbox);
        outboxRepository.save(outbox);
    }
}
