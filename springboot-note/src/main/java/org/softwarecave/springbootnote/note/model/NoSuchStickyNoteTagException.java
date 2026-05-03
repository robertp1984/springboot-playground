package org.softwarecave.springbootnote.note.model;

public class NoSuchStickyNoteTagException extends RuntimeException {
    public NoSuchStickyNoteTagException(String message) {
        super(message);
    }
}
