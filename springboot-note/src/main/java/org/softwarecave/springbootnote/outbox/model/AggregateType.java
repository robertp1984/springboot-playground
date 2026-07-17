package org.softwarecave.springbootnote.outbox.model;

import lombok.Getter;
import org.apache.avro.specific.SpecificRecord;
import org.softwarecave.springbootnote.avro.StickyNote;

@Getter
public enum AggregateType {
    STICKY_NOTE(StickyNote.class);

    private final Class<? extends SpecificRecord> avroClass;

    AggregateType(Class<? extends SpecificRecord> avroClass) {
        this.avroClass = avroClass;
    }

}
