package org.softwarecave.springbootnote.note.web.converter;

import org.junit.jupiter.api.Test;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.model.StickyNoteLink;
import org.softwarecave.springbootnote.note.model.StickyNoteTag;
import org.softwarecave.springbootnote.note.model.Type;
import org.softwarecave.springbootnote.note.web.StickyNoteDTO;
import org.softwarecave.springbootnote.note.web.StickyNoteLinkDTO;
import org.softwarecave.springbootnote.note.web.StickyNoteTagDTO;
import org.softwarecave.springbootnote.tag.model.Tag;
import org.softwarecave.springbootnote.tag.web.TagDTO;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class StickyNoteConverterTest {

    @Test
    public void convertToDTO_returnsDTOWithEmptyLinkAndTagLists() {
        StickyNote note = new StickyNote(1L, "Title", "Body", Type.PLAIN_TEXT, null, null, LocalDateTime.now());

        StickyNoteConverter converter = new StickyNoteConverter();

        StickyNoteDTO dto = converter.convertToDTO(note);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Title");
        assertThat(dto.getBody()).isEqualTo("Body");
        assertThat(dto.getType()).isEqualTo(Type.PLAIN_TEXT);
        assertThat(dto.getLinks()).isEmpty();
        assertThat(dto.getTags()).isEmpty();
        assertThat(dto.getCreated()).isEqualTo(note.getCreated());
    }

    @Test
    public void convertToDTO_withMultipleLinksAndTags_convertsAll() {
        StickyNote note = new StickyNote();
        note.setId(2L);
        note.setTitle("T");
        note.setBody("B");
        note.setType(Type.PLAIN_TEXT);
        note.setCreated(LocalDateTime.now());

        StickyNoteLink l1 = new StickyNoteLink(10L, note, "http://a");
        StickyNoteLink l2 = new StickyNoteLink(11L, note, "http://b");
        note.setLinks(Arrays.asList(l1, l2));

        StickyNoteTag t1 = new StickyNoteTag(20L, note, new Tag(30L, "Kafka", "Kafka Platform"));
        StickyNoteTag t2 = new StickyNoteTag(21L, note, new Tag(31L, "Kafka Connect", "Kafka Connect"));
        StickyNoteTag t3 = new StickyNoteTag(23L, note, new Tag(32L, "Kafka Stream", "Kafka Stream"));
        note.setTags(Arrays.asList(t1, t2, t3));

        StickyNoteConverter converter = new StickyNoteConverter();

        StickyNoteDTO dto = converter.convertToDTO(note);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getLinks()).hasSize(2);
        assertThat(dto.getLinks()).extracting(StickyNoteLinkDTO::getLink).containsExactlyInAnyOrder("http://a", "http://b");
        assertThat(dto.getTags()).hasSize(3);
        assertThat(dto.getTags()).extracting(e -> e.getTag().getName()).containsExactlyInAnyOrder("Kafka", "Kafka Connect", "Kafka Stream");
    }

    @Test
    public void convertToEntity_returnsEntityWithEmptyLinksAngTagList() {
        LocalDateTime now = LocalDateTime.now();
        StickyNoteDTO dto = new StickyNoteDTO(3L, "T2", "B2", Type.PLAIN_TEXT, null, null, now);

        StickyNoteConverter converter = new StickyNoteConverter();

        StickyNote entity = converter.convertToEntity(dto);

        assertThat(entity.getId()).isEqualTo(3L);
        assertThat(entity.getTitle()).isEqualTo("T2");
        assertThat(entity.getBody()).isEqualTo("B2");
        assertThat(entity.getType()).isEqualTo(Type.PLAIN_TEXT);
        assertThat(entity.getLinks()).isEmpty();
        assertThat(entity.getTags()).isEmpty();
        assertThat(entity.getCreated()).isEqualTo(now);
    }

    @Test
    public void convertToEntity_withLinksAndTags_convertsAllLinksAndTagsToEntities() {
        LocalDateTime now = LocalDateTime.now();
        StickyNoteLinkDTO ld1 = new StickyNoteLinkDTO(20L, 4L, "http://x");
        StickyNoteLinkDTO ld2 = new StickyNoteLinkDTO(21L, 4L, "http://y");
        StickyNoteTagDTO td1 = new StickyNoteTagDTO(30L, 4L, new TagDTO(40L, "Kafka", "Kafka Platform"));
        StickyNoteTagDTO td2 = new StickyNoteTagDTO(31L, 4L, new TagDTO(41L, "Kafka Stream", "Kafka Stream"));
        StickyNoteDTO dto = new StickyNoteDTO(4L, "TT", "BB", Type.PLAIN_TEXT, Arrays.asList(ld1, ld2), Arrays.asList(td1, td2), now);

        StickyNoteConverter converter = new StickyNoteConverter();

        StickyNote entity = converter.convertToEntity(dto);

        assertThat(entity.getId()).isEqualTo(4L);
        assertThat(entity.getLinks()).hasSize(2);
        assertThat(entity.getLinks()).extracting(StickyNoteLink::getLink).containsExactlyInAnyOrder("http://x", "http://y");
        assertThat(entity.getTags()).hasSize(2);
        assertThat(entity.getTags()).extracting(t -> t.getTag().getName()).containsExactlyInAnyOrder("Kafka", "Kafka Stream");
    }
}
