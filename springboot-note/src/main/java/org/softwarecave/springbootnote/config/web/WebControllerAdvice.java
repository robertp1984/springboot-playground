package org.softwarecave.springbootnote.config.web;

import org.softwarecave.springbootnote.note.model.NoSuchStickyNoteException;
import org.softwarecave.springbootnote.note.model.StickyNoteValidationException;
import org.softwarecave.springbootnote.tag.model.NoSuchTagException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class WebControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleNoSuchStickyNoteException(NoSuchStickyNoteException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleNoSuchTagException(NoSuchTagException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler
    public ResponseEntity<String> handleStickyNoteValidationException(StickyNoteValidationException ex) {
        return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
    }

}
