package org.softwarecave.springbootnote.tag.web.converter;

import org.softwarecave.springbootnote.tag.model.Tag;
import org.softwarecave.springbootnote.tag.web.TagDTO;

public class TagConverter {
    public TagDTO convertToDTO(Tag tag) {
        return new TagDTO(tag.getId(), tag.getName(), tag.getDescription());
    }

    public Tag convertToEntity(TagDTO dto) {
        return new Tag(dto.getId(), dto.getName(), dto.getDescription());
    }
}
