package org.softwarecave.springbootnote.note.web.converter;

import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.web.StickyNoteDTO;
import org.softwarecave.springbootnote.note.web.StickyNoteLinkDTO;

import java.util.ArrayList;
import java.util.List;

public class StickyNoteConverter {
    public StickyNoteDTO convertToDTO(StickyNote note) {
        StickyNoteLinkConverter linkConverter = new StickyNoteLinkConverter(note);
        List<StickyNoteLinkDTO> links = new ArrayList<>();
        if (note.getLinks() != null) {
            note.getLinks().stream().map(linkConverter::convertToDTO).forEach(links::add);
        }

        return new StickyNoteDTO(note.getId(), note.getTitle(), note.getBody(), note.getType(), links, note.getCreated());
    }

    public StickyNote convertToEntity(StickyNoteDTO noteDTO) {
        StickyNote note = new StickyNote(noteDTO.getId(), noteDTO.getTitle(), noteDTO.getBody(), noteDTO.getType(), new ArrayList<>(), noteDTO.getCreated());

        StickyNoteLinkConverter linkConverter = new StickyNoteLinkConverter(note);
        if (noteDTO.getLinks() != null) {
            noteDTO.getLinks().stream().map(linkConverter::convertToEntity).forEach(e -> note.getLinks().add(e));
        }
        return note;
    }
}
