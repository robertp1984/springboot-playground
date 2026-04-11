package org.softwarecave.springbootnote.note.model;

public class NoSuchStickyNoteException extends RuntimeException {
    public NoSuchStickyNoteException(String s) {
        super(s);
    }
}
