package org.softwarecave.springbootnote.tag.web.converter;

import org.junit.jupiter.api.Test;
import org.softwarecave.springbootnote.tag.model.Tag;
import org.softwarecave.springbootnote.tag.web.TagDTO;

import static org.junit.jupiter.api.Assertions.*;


class TagConverterTest {

    private final TagConverter converter = new TagConverter();

    @Test
    void convertToDTO_mapsAllFields() {
        Tag tag = new Tag(123L, "important", "very important notes");

        TagDTO dto = converter.convertToDTO(tag);

        assertNotNull(dto);
        assertEquals(123L, dto.getId());
        assertEquals("important", dto.getName());
        assertEquals("very important notes", dto.getDescription());
    }

    @Test
    void convertToEntity_mapsAllFields() {
        TagDTO dto = new TagDTO(99L, "todo", "things to do");

        Tag entity = converter.convertToEntity(dto);

        assertNotNull(entity);
        assertEquals(99L, entity.getId());
        assertEquals("todo", entity.getName());
        assertEquals("things to do", entity.getDescription());
    }
}


