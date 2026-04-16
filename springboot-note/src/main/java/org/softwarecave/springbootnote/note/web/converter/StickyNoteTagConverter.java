package org.softwarecave.springbootnote.note.web.converter;

import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.model.StickyNoteTag;
import org.softwarecave.springbootnote.tag.model.Tag;
import org.softwarecave.springbootnote.tag.web.converter.TagConverter;
import org.softwarecave.springbootnote.note.web.StickyNoteTagDTO;
import org.softwarecave.springbootnote.tag.web.TagDTO;

import static org.softwarecave.springbootnote.note.web.converter.StickyNoteConverter.verifyId;

public class StickyNoteTagConverter {

    private final StickyNote stickyNote;
    private final TagConverter tagConverter;

    public StickyNoteTagConverter(StickyNote stickyNote) {
        this.stickyNote = stickyNote;
        this.tagConverter = new TagConverter();
    }

    public StickyNoteTagDTO convertToDTO(StickyNoteTag tag) {
        Long noteId = tag.getStickyNote() != null ? tag.getStickyNote().getId() : null;
        TagDTO tagDTO = tagConverter.convertToDTO(tag.getTag());
        return new StickyNoteTagDTO(tag.getId(), noteId, tagDTO);
    }

    public StickyNoteTag convertToEntity(StickyNoteTagDTO dto) {
        if (verifyId(stickyNote, dto.getStickyNoteId())) {
            Tag tag = tagConverter.convertToEntity(dto.getTag());
            return new StickyNoteTag(dto.getId(), stickyNote, tag);
        } else {
            throw new IllegalArgumentException("StickyNote IDs are not equal");
        }
    }
}
