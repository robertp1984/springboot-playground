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
import java.util.Objects;
import java.util.Optional;

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
    public StickyNote getStickyNoteById(Long stickyNoteId) throws NoSuchStickyNoteException {
        if (stickyNoteId == null) {
            throw new StickyNoteValidationException("No sticky note id provided");
        }
        Optional<StickyNote> stickyNote = stickyNoteRepository.findById(stickyNoteId);
        return stickyNote.orElseThrow(() -> new NoSuchStickyNoteException("No sticky note found for ID " + stickyNoteId));
    }

    @Override
    @Recordable(modelType = ModelType.STICKY_NOTE, actionType = ActionType.ADD)
    public StickyNote addStickyNote(StickyNote stickyNote) {
        Objects.requireNonNull(stickyNote);
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
        Objects.requireNonNull(stickyNote);
        if (stickyNote.getId() == null) {
            throw new StickyNoteValidationException("Sticky note to update has null id");
        }
        if (stickyNoteRepository.existsById(stickyNote.getId())) {

            stickyNoteProcessor.processLinks(stickyNote, false);
            stickyNoteProcessor.processTags(stickyNote, false);
            return stickyNoteRepository.save(stickyNote);
        } else {
            throw new NoSuchStickyNoteException("No sticky note found for ID " + stickyNote.getId());
        }
    }

    @Override
    @Recordable(modelType = ModelType.STICKY_NOTE, actionType = ActionType.DELETE)
    public void deleteStickyNote(Long stickyNoteId) {
        if (stickyNoteId == null) {
            throw new StickyNoteValidationException("No sticky note id provided");
        }
        if (stickyNoteRepository.existsById(stickyNoteId)) {
            stickyNoteRepository.deleteById(stickyNoteId);
        } else {
            throw new NoSuchStickyNoteException("No sticky note found for ID " + stickyNoteId);
        }
    }

}
