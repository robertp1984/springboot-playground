package org.softwarecave.springbootnote.note.model;

public class StickyNoteValidationException extends RuntimeException {
    public StickyNoteValidationException(String message) {
        super(message);
    }
}
