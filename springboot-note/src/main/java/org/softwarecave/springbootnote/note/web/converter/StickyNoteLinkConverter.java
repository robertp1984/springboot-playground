package org.softwarecave.springbootnote.note.web.converter;

import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.model.StickyNoteLink;
import org.softwarecave.springbootnote.note.web.StickyNoteLinkDTO;

import java.util.Objects;

import static org.softwarecave.springbootnote.note.web.converter.StickyNoteConverter.verifyId;

public class StickyNoteLinkConverter {

    private final StickyNote stickyNote;

    public StickyNoteLinkConverter(StickyNote stickyNote) {
        this.stickyNote = stickyNote;
    }

    public StickyNoteLinkDTO convertToDTO(StickyNoteLink link) {
        Long noteId = link.getStickyNote() != null ? link.getStickyNote().getId() : null;
        return new StickyNoteLinkDTO(link.getId(), noteId, link.getLink());
    }

    public StickyNoteLink convertToEntity(StickyNoteLinkDTO dto) {
        if (verifyId(stickyNote, dto.getStickyNoteId())) {
            return new StickyNoteLink(dto.getId(), stickyNote, dto.getLink());
        } else {
            throw new IllegalArgumentException("StickyNote IDs are not equal");
        }
    }
}
