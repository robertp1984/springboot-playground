package org.softwarecave.springbootnote.notification.kafka.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.model.StickyNoteLink;
import org.softwarecave.springbootnote.note.model.StickyNoteTag;
import org.softwarecave.springbootnote.note.model.Type;
import org.softwarecave.springbootnote.tag.model.Tag;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AvroStickyNoteConverterTest {

    private AvroStickyNoteConverter converter;

    @BeforeEach
    public void setup() {
        this.converter = new AvroStickyNoteConverter();
    }

    @Test
    public void testConvert_WithNoChildren() {
        var created = LocalDateTime.of(2026, 2, 5, 10, 5);
        var stickyNote = new StickyNote(5L, "Title", "Body", Type.JSON, List.of(), List.of(), created);

        var avroStickyNote = converter.convertToAvro(stickyNote);

        var createdMillis = created.toInstant(ZoneOffset.UTC).toEpochMilli();
        assertThat(avroStickyNote)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 5L)
                .hasFieldOrPropertyWithValue("title", "Title")
                .hasFieldOrPropertyWithValue("body", "Body")
                .hasFieldOrPropertyWithValue("created", createdMillis);
    }

    @Test
    public void testConvert_WithLinksAndTags() {
        var created = LocalDateTime.of(2026, 2, 5, 10, 5);
        var tag1 = new Tag(21L, "GitHub", "GitHub portal");
        var tag2 = new Tag(22L, "Git", "Git SCM");
        var stickyNote = new StickyNote(5L, "Title", "Body", Type.JSON, List.of(), List.of(),
                LocalDateTime.of(2026, 2, 5, 10, 5));
        stickyNote.setLinks(List.of(
                new StickyNoteLink(11L, stickyNote, "http://google.pl"),
                new StickyNoteLink(12L, stickyNote, "http://github.com")
        ));
        stickyNote.setTags(List.of(
                new StickyNoteTag(41L, stickyNote, tag1),
                new StickyNoteTag(42L, stickyNote, tag2)
        ));

        var avroStickyNote = converter.convertToAvro(stickyNote);

        var createdMillis = created.toInstant(ZoneOffset.UTC).toEpochMilli();
        assertThat(avroStickyNote)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 5L)
                .hasFieldOrPropertyWithValue("title", "Title")
                .hasFieldOrPropertyWithValue("body", "Body")
                .hasFieldOrPropertyWithValue("created", createdMillis);

        assertThat(avroStickyNote.getLinks())
                .hasSize(2);
        assertThat(avroStickyNote.getLinks().get(0))
                .hasFieldOrPropertyWithValue("id", 11L)
                .hasFieldOrPropertyWithValue("stickyNoteId", 5L)
                .hasFieldOrPropertyWithValue("link", "http://google.pl");
        assertThat(avroStickyNote.getLinks().get(1))
                .hasFieldOrPropertyWithValue("id", 12L)
                .hasFieldOrPropertyWithValue("stickyNoteId", 5L)
                .hasFieldOrPropertyWithValue("link", "http://github.com");

        assertThat(avroStickyNote.getTags())
                .hasSize(2);
        assertThat(avroStickyNote.getTags().get(0))
                .hasFieldOrPropertyWithValue("id", 41L)
                .hasFieldOrPropertyWithValue("stickyNoteId", 5L);
        assertThat(avroStickyNote.getTags().get(1))
                .hasFieldOrPropertyWithValue("id", 42L)
                .hasFieldOrPropertyWithValue("stickyNoteId", 5L);

        assertThat(avroStickyNote.getTags().get(0).getTag())
                .hasFieldOrPropertyWithValue("id", 21L)
                .hasFieldOrPropertyWithValue("name", "GitHub")
                .hasFieldOrPropertyWithValue("description", "GitHub portal");
        assertThat(avroStickyNote.getTags().get(1).getTag())
                .hasFieldOrPropertyWithValue("id", 22L)
                .hasFieldOrPropertyWithValue("name", "Git")
                .hasFieldOrPropertyWithValue("description", "Git SCM");
    }
}
