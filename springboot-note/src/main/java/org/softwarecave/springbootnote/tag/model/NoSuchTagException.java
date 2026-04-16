package org.softwarecave.springbootnote.tag.model;

public class NoSuchTagException extends RuntimeException {
    public NoSuchTagException(String message) {
        super(message);
    }
}
