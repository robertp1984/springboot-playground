package org.softwarecave.springbootnote.notification.kafka;

import org.softwarecave.springbootnote.note.model.StickyNote;

public interface KafkaStickyNoteProducer {
    void sendToKafka(StickyNote value);
}
