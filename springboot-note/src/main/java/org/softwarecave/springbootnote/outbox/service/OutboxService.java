package org.softwarecave.springbootnote.outbox.service;

import org.softwarecave.springbootnote.note.model.StickyNote;

public interface OutboxService {
    void send(StickyNote stickyNote);
}
