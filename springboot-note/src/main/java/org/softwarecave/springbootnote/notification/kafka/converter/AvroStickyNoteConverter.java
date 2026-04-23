package org.softwarecave.springbootnote.notification.kafka.converter;

import lombok.extern.slf4j.Slf4j;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.tag.model.Tag;

import java.time.ZoneOffset;
import java.util.List;

@Slf4j
public class AvroStickyNoteConverter {
    public org.softwarecave.springbootnote.avro.StickyNote convertToAvro(StickyNote value) {
        var linksAvro = convertLinksToAvro(value);
        var tagsAvro = convertTagsToAvro(value);

        return org.softwarecave.springbootnote.avro.StickyNote.newBuilder()
                .setId(value.getId())
                .setTitle(value.getTitle())
                .setBody(value.getBody())
                .setType(value.getType().name())
                .setCreated(value.getCreated().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setLinks(linksAvro)
                .setTags(tagsAvro)
                .build();
    }

    private List<org.softwarecave.springbootnote.avro.StickyNoteTag> convertTagsToAvro(StickyNote value) {
        return value.getTags().stream()
                .map(t -> org.softwarecave.springbootnote.avro.StickyNoteTag.newBuilder()
                        .setId(t.getId())
                        .setStickyNoteId(t.getStickyNote().getId())
                        .setTag(convertTagToAvro(t.getTag()))
                        .build())
                .toList();
    }

    private org.softwarecave.springbootnote.avro.Tag convertTagToAvro(Tag tag) {
        return org.softwarecave.springbootnote.avro.Tag.newBuilder()
                .setId(tag.getId())
                .setName(tag.getName())
                .setDescription(tag.getDescription())
                .build();
    }

    private List<org.softwarecave.springbootnote.avro.StickyNoteLink> convertLinksToAvro(StickyNote value) {
        return value.getLinks().stream()
                .map(link -> org.softwarecave.springbootnote.avro.StickyNoteLink.newBuilder()
                        .setId(link.getId())
                        .setStickyNoteId(link.getStickyNote().getId())
                        .setLink(link.getLink())
                        .build()
                ).toList();
    }
}
