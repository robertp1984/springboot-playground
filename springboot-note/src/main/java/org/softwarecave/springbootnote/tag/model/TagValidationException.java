package org.softwarecave.springbootnote.tag.model;

public class TagValidationException extends RuntimeException {
    public TagValidationException(String message) {
        super(message);
    }
}
