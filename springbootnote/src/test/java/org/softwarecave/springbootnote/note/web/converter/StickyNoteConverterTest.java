package org.softwarecave.springbootnote.note.web.converter;

import org.junit.jupiter.api.Test;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.model.StickyNoteLink;
import org.softwarecave.springbootnote.note.model.Type;
import org.softwarecave.springbootnote.note.web.StickyNoteDTO;
import org.softwarecave.springbootnote.note.web.StickyNoteLinkDTO;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class StickyNoteConverterTest {

    @Test
    public void convertToDTO_withNoLinks_returnsDTOWithEmptyLinksList() {
        StickyNote note = new StickyNote(1L, "Title", "Body", Type.PLAIN_TEXT, null, LocalDateTime.now());

        StickyNoteConverter converter = new StickyNoteConverter();

        StickyNoteDTO dto = converter.convertToDTO(note);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Title");
        assertThat(dto.getBody()).isEqualTo("Body");
        assertThat(dto.getType()).isEqualTo(Type.PLAIN_TEXT);
        assertThat(dto.getStickyNoteLinks()).isEmpty();
        assertThat(dto.getCreated()).isEqualTo(note.getCreated());
    }

    @Test
    public void convertToDTO_withMultipleLinks_convertsAllLinks() {
        StickyNote note = new StickyNote();
        note.setId(2L);
        note.setTitle("T");
        note.setBody("B");
        note.setType(Type.PLAIN_TEXT);
        note.setCreated(LocalDateTime.now());

        StickyNoteLink l1 = new StickyNoteLink(10L, note, "http://a");
        StickyNoteLink l2 = new StickyNoteLink(11L, note, "http://b");
        note.setLinks(Arrays.asList(l1, l2));

        StickyNoteConverter converter = new StickyNoteConverter();

        StickyNoteDTO dto = converter.convertToDTO(note);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getStickyNoteLinks()).hasSize(2);
        assertThat(dto.getStickyNoteLinks()).extracting(StickyNoteLinkDTO::getLink).containsExactlyInAnyOrder("http://a", "http://b");
    }

    @Test
    public void convertToEntity_withNoLinks_returnsEntityWithEmptyLinksList() {
        LocalDateTime now = LocalDateTime.now();
        StickyNoteDTO dto = new StickyNoteDTO(3L, "T2", "B2", Type.PLAIN_TEXT, null, now);

        StickyNoteConverter converter = new StickyNoteConverter();

        StickyNote entity = converter.convertToEntity(dto);

        assertThat(entity.getId()).isEqualTo(3L);
        assertThat(entity.getTitle()).isEqualTo("T2");
        assertThat(entity.getBody()).isEqualTo("B2");
        assertThat(entity.getType()).isEqualTo(Type.PLAIN_TEXT);
        assertThat(entity.getLinks()).isEmpty();
        assertThat(entity.getCreated()).isEqualTo(now);
    }

    @Test
    public void convertToEntity_withLinks_convertsAllLinksToEntities() {
        LocalDateTime now = LocalDateTime.now();
        StickyNoteLinkDTO ld1 = new StickyNoteLinkDTO(20L, 4L, "http://x");
        StickyNoteLinkDTO ld2 = new StickyNoteLinkDTO(21L, 4L, "http://y");
        StickyNoteDTO dto = new StickyNoteDTO(4L, "TT", "BB", Type.PLAIN_TEXT, Arrays.asList(ld1, ld2), now);

        StickyNoteConverter converter = new StickyNoteConverter();

        StickyNote entity = converter.convertToEntity(dto);

        assertThat(entity.getId()).isEqualTo(4L);
        assertThat(entity.getLinks()).hasSize(2);
        assertThat(entity.getLinks()).extracting(StickyNoteLink::getLink).containsExactlyInAnyOrder("http://x", "http://y");
    }
}
