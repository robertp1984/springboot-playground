package org.softwarecave.springbootnote.note.web.converter;

import org.junit.jupiter.api.Test;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.model.StickyNoteLink;
import org.softwarecave.springbootnote.note.web.StickyNoteLinkDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StickyNoteLinkConverterTest {

    @Test
    public void convertToDTO_withStickyNote_returnsDTOWithNoteId() {
        StickyNote parent = new StickyNote();
        parent.setId(42L);
        StickyNoteLink link = new StickyNoteLink(7L, parent, "http://example.com");
        StickyNoteLinkConverter converter = new StickyNoteLinkConverter(parent);

        StickyNoteLinkDTO dto = converter.convertToDTO(link);

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getStickyNoteId()).isEqualTo(42L);
        assertThat(dto.getLink()).isEqualTo("http://example.com");
    }

    @Test
    public void convertToDTO_withNullStickyNoteOnLink_returnsDTOWithNullNoteId() {
        StickyNoteLink link = new StickyNoteLink(8L, null, "http://nolink.com");
        StickyNoteLinkConverter converter = new StickyNoteLinkConverter(null);

        StickyNoteLinkDTO dto = converter.convertToDTO(link);

        assertThat(dto.getId()).isEqualTo(8L);
        assertThat(dto.getStickyNoteId()).isNull();
        assertThat(dto.getLink()).isEqualTo("http://nolink.com");
    }

    @Test
    public void convertToEntity_converterStickyNoteNull_andDtoStickyNoteIdNull_returnsEntityWithNullStickyNote() {
        StickyNoteLinkConverter converter = new StickyNoteLinkConverter(null);
        StickyNoteLinkDTO dto = new StickyNoteLinkDTO(9L, null, "http://any.com");

        StickyNoteLink entity = converter.convertToEntity(dto);

        assertThat(entity.getId()).isEqualTo(9L);
        assertThat(entity.getStickyNote()).isNull();
        assertThat(entity.getLink()).isEqualTo("http://any.com");
    }

    @Test
    public void convertToEntity_bothStickyNoteIdsNull_returnsEntityWithConverterStickyNote() {
        StickyNote parent = new StickyNote();
        parent.setId(null);
        StickyNoteLinkConverter converter = new StickyNoteLinkConverter(parent);
        StickyNoteLinkDTO dto = new StickyNoteLinkDTO(11L, null, "http://bothnull.com");

        StickyNoteLink entity = converter.convertToEntity(dto);

        assertThat(entity.getId()).isEqualTo(11L);
        assertThat(entity.getStickyNote()).isSameAs(parent);
        assertThat(entity.getLink()).isEqualTo("http://bothnull.com");
    }

    @Test
    public void convertToEntity_idsMatch_returnsEntityWithConverterStickyNote() {
        StickyNote parent = new StickyNote();
        parent.setId(100L);
        StickyNoteLinkConverter converter = new StickyNoteLinkConverter(parent);
        StickyNoteLinkDTO dto = new StickyNoteLinkDTO(12L, 100L, "http://match.com");

        StickyNoteLink entity = converter.convertToEntity(dto);

        assertThat(entity.getId()).isEqualTo(12L);
        assertThat(entity.getStickyNote()).isSameAs(parent);
        assertThat(entity.getLink()).isEqualTo("http://match.com");
    }

    @Test
    public void convertToEntity_idsMismatch_throwsIllegalArgumentException() {
        StickyNote parent = new StickyNote();
        parent.setId(1L);
        StickyNoteLinkConverter converter = new StickyNoteLinkConverter(parent);
        StickyNoteLinkDTO dto = new StickyNoteLinkDTO(10L, 2L, "http://mismatch.com");

        assertThrows(IllegalArgumentException.class, () -> converter.convertToEntity(dto));
    }
}

