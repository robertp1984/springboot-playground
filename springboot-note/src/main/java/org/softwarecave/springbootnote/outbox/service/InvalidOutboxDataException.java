package org.softwarecave.springbootnote.outbox.service;

public class InvalidOutboxDataException extends RuntimeException {
    public InvalidOutboxDataException(String message) {
        super(message);
    }
}
