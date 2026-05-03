package org.softwarecave.springbootnote.note.web;

import jakarta.validation.Valid;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.service.StickyNoteService;
import org.softwarecave.springbootnote.note.web.converter.StickyNoteConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping(path = "/api/v1/stickyNotes")
public class StickyNoteController {

    private final StickyNoteService stickyNoteService;
    private final StickyNoteConverter converter;

    public StickyNoteController(StickyNoteService stickyNoteService,
                                StickyNoteConverter converter) {
        this.stickyNoteService = stickyNoteService;
        this.converter = converter;
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<StickyNoteDTO> getAllStickyNotes() {
        var stickyNotes = stickyNoteService.getStickyNotes();

        return stickyNotes.stream().map(converter::convertToDTO).toList();
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StickyNoteDTO getStickyNote(@PathVariable("id") Long id) {
        var stickyNote = stickyNoteService.getStickyNoteById(id);

        return converter.convertToDTO(stickyNote);
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StickyNoteDTO> addStickyNote(@Valid @RequestBody StickyNoteDTO stickyNoteDTO) {
        var stickyNote = converter.convertToEntity(stickyNoteDTO);

        var addedStickyNote = stickyNoteService.addStickyNote(stickyNote);

        var addedStickyNoteDTO = converter.convertToDTO(addedStickyNote);
        URI locationUri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}").buildAndExpand(addedStickyNote.getId()).toUri();
        return ResponseEntity.created(locationUri).body(addedStickyNoteDTO);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public StickyNoteDTO updateStickyNote(@PathVariable("id") Long id, @Valid @RequestBody StickyNoteDTO stickyNoteDTO) {
        if (stickyNoteDTO.getId() != null && !stickyNoteDTO.getId().equals(id)) {
            throw new IllegalArgumentException("StickyNote IDs are not equal");
        }
        stickyNoteDTO.setId(id);

        StickyNote stickyNote = converter.convertToEntity(stickyNoteDTO);

        StickyNote updatedStickyNote = stickyNoteService.updateStickyNote(stickyNote);
        return converter.convertToDTO(updatedStickyNote);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStickyNote(@PathVariable("id") Long id) {
        stickyNoteService.deleteStickyNote(id);
    }

}
