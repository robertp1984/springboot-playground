package org.softwarecave.springbootnote.note.service;

import org.softwarecave.springbootnote.note.model.NoSuchStickyNoteException;
import org.softwarecave.springbootnote.note.model.StickyNote;

import java.util.List;

public interface StickyNoteService {
    List<StickyNote> getStickyNotes();

    StickyNote getStickyNoteById(Long stickyNoteId) throws NoSuchStickyNoteException;

    StickyNote addStickyNote(StickyNote stickyNote);

    StickyNote updateStickyNote(StickyNote stickyNote);

    void deleteStickyNote(Long stickyNoteId);
}
