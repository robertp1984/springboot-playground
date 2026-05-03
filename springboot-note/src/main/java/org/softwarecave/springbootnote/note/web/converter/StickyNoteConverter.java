package org.softwarecave.springbootnote.note.web.converter;

import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.web.StickyNoteDTO;
import org.softwarecave.springbootnote.note.web.StickyNoteLinkDTO;
import org.softwarecave.springbootnote.note.web.StickyNoteTagDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class StickyNoteConverter {
    public StickyNoteDTO convertToDTO(StickyNote note) {
        StickyNoteLinkConverter linkConverter = new StickyNoteLinkConverter(note);
        StickyNoteTagConverter tagConverter = new StickyNoteTagConverter(note);
        List<StickyNoteLinkDTO> links = new ArrayList<>();
        if (note.getLinks() != null) {
            note.getLinks().stream().map(linkConverter::convertToDTO).forEach(links::add);
        }
        List<StickyNoteTagDTO> tags = new ArrayList<>();
        if (note.getTags() != null) {
            note.getTags().stream().map(tagConverter::convertToDTO).forEach(tags::add);
        }

        return new StickyNoteDTO(note.getId(), note.getTitle(), note.getBody(), note.getType(), links, tags, note.getCreated());
    }

    public StickyNote convertToEntity(StickyNoteDTO noteDTO) {
        StickyNote note = new StickyNote(noteDTO.getId(), noteDTO.getTitle(), noteDTO.getBody(), noteDTO.getType(), new ArrayList<>(), new ArrayList<>(), noteDTO.getCreated());

        StickyNoteLinkConverter linkConverter = new StickyNoteLinkConverter(note);
        if (noteDTO.getLinks() != null) {
            noteDTO.getLinks().stream().map(linkConverter::convertToEntity).forEach(e -> note.getLinks().add(e));
        }
        StickyNoteTagConverter tagConverter = new StickyNoteTagConverter(note);
        if (noteDTO.getTags() != null) {
            noteDTO.getTags().stream().map(tagConverter::convertToEntity).forEach(e -> note.getTags().add(e));
        }

        return note;
    }

    public static boolean verifyId(StickyNote stickyNote, Long stickyNoteId) {
        return stickyNote == null
                || (stickyNoteId == null && stickyNote.getId() == null)
                || (stickyNoteId != null && Objects.equals(stickyNoteId, stickyNote.getId()));
    }
}
