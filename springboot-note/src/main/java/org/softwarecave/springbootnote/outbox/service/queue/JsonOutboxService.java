package org.softwarecave.springbootnote.outbox.service.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.web.StickyNoteDTO;
import org.softwarecave.springbootnote.note.web.converter.StickyNoteConverter;
import org.softwarecave.springbootnote.outbox.model.AggregateType;
import org.softwarecave.springbootnote.outbox.model.MessageType;
import org.softwarecave.springbootnote.outbox.model.Outbox;
import org.softwarecave.springbootnote.outbox.model.Status;
import org.softwarecave.springbootnote.outbox.service.OutboxRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBooleanProperty(prefix = "app.kafka.json", name = "enabled", havingValue = true)
public class JsonOutboxService implements OutboxService {

    private final OutboxRepository outboxRepository;
    private final JsonMapper jsonMapper;
    private final StickyNoteConverter stickyNoteConverter;

    @Transactional
    @Override
    public void send(StickyNote stickyNote) {

        StickyNoteDTO dto = stickyNoteConverter.convertToDTO(stickyNote);
        String payloadString = jsonMapper.writeValueAsString(dto);

        Outbox outbox = new Outbox(null, AggregateType.STICKY_NOTE, stickyNote.getId(),
                MessageType.JSON, ZonedDateTime.now(), null, payloadString, Status.NEW);

        log.info("Saving to outbox: {}", outbox);
        outboxRepository.save(outbox);
    }

}
