package org.softwarecave.springbootnotecategorizer.categorizer;

public class CategorizationException extends RuntimeException {
    public CategorizationException(String message) {
        super(message);
    }

    public CategorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}

