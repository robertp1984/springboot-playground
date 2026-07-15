package org.softwarecave.springbootnote.outbox.service.queue;

import org.softwarecave.springbootnote.note.model.StickyNote;

public interface OutboxService {
    void send(StickyNote stickyNote);
}
