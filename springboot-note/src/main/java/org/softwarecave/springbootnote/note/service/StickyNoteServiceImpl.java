package org.softwarecave.springbootnote.note.service;

import org.softwarecave.springbootnote.note.model.NoSuchStickyNoteException;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.model.StickyNoteValidationException;
import org.softwarecave.springbootnote.notification.ActionType;
import org.softwarecave.springbootnote.notification.ModelType;
import org.softwarecave.springbootnote.notification.Recordable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StickyNoteServiceImpl implements StickyNoteService {

    private final StickyNoteRepository stickyNoteRepository;
    private final StickyNoteProcessor stickyNoteProcessor;

    public StickyNoteServiceImpl(StickyNoteRepository stickyNoteRepository,
                                 StickyNoteProcessor stickyNoteProcessor) {
        this.stickyNoteRepository = stickyNoteRepository;
        this.stickyNoteProcessor = stickyNoteProcessor;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StickyNote> getStickyNotes() {
        return stickyNoteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public StickyNote getStickyNoteById(Long stickyNoteId) {
        if (stickyNoteId == null) {
            throw new StickyNoteValidationException("No sticky note id provided");
        }
        return stickyNoteRepository.findById(stickyNoteId)
                .orElseThrow(() -> new NoSuchStickyNoteException("No sticky note found for ID " + stickyNoteId));
    }

    @Override
    @Recordable(modelType = ModelType.STICKY_NOTE, actionType = ActionType.ADD)
    public StickyNote addStickyNote(StickyNote stickyNote) {
        if (stickyNote == null) {
            throw new StickyNoteValidationException("Sticky note is null");
        }
        if (stickyNote.getId() != null) {
            throw new StickyNoteValidationException("New sticky note has not null id");
        }

        stickyNoteProcessor.processLinks(stickyNote, true);
        stickyNoteProcessor.processTags(stickyNote, true);
        return stickyNoteRepository.save(stickyNote);
    }

    @Override
    @Recordable(modelType = ModelType.STICKY_NOTE, actionType = ActionType.UPDATE)
    public StickyNote updateStickyNote(StickyNote stickyNote) {
        if (stickyNote == null) {
            throw new StickyNoteValidationException("Sticky note is null");
        }
        if (stickyNote.getId() == null) {
            throw new StickyNoteValidationException("Sticky note to update has null id");
        }

        var stickyNoteId = stickyNote.getId();
        StickyNote existing = stickyNoteRepository.findById(stickyNoteId)
                .orElseThrow(() -> new NoSuchStickyNoteException("No sticky note found for ID " + stickyNoteId));

        existing.setTitle(stickyNote.getTitle());
        existing.setBody(stickyNote.getBody());
        existing.setType(stickyNote.getType());
        existing.getLinks().clear();
        existing.getLinks().addAll(stickyNote.getLinks());
        existing.getTags().clear();
        existing.getTags().addAll(stickyNote.getTags());

        stickyNoteProcessor.processLinks(existing, false);
        stickyNoteProcessor.processTags(existing, false);

        return stickyNoteRepository.save(existing);
    }

    @Override
    @Recordable(modelType = ModelType.STICKY_NOTE, actionType = ActionType.DELETE)
    public void deleteStickyNote(Long stickyNoteId) {
        if (stickyNoteId == null) {
            throw new StickyNoteValidationException("No sticky note id provided");
        }
        StickyNote existing = stickyNoteRepository.findById(stickyNoteId)
                .orElseThrow(() -> new NoSuchStickyNoteException("No sticky note found for ID " + stickyNoteId));
        stickyNoteRepository.delete(existing);
    }

}
