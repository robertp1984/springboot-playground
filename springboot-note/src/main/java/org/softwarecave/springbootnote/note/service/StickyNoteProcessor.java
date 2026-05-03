package org.softwarecave.springbootnote.note.service;

import org.softwarecave.springbootnote.note.model.NoSuchStickyNoteException;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.model.StickyNoteLink;
import org.softwarecave.springbootnote.note.model.StickyNoteTag;
import org.softwarecave.springbootnote.note.model.StickyNoteValidationException;
import org.softwarecave.springbootnote.tag.model.NoSuchTagException;
import org.softwarecave.springbootnote.tag.service.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class StickyNoteProcessor {
    private final StickyNoteLinkRepository stickyNoteLinkRepository;
    private final StickyNoteTagRepository stickyNoteTagRepository;
    private final TagRepository tagRepository;

    public StickyNoteProcessor(StickyNoteLinkRepository stickyNoteLinkRepository,
                               StickyNoteTagRepository stickyNoteTagRepository,
                               TagRepository tagRepository) {
        this.stickyNoteLinkRepository = stickyNoteLinkRepository;
        this.stickyNoteTagRepository = stickyNoteTagRepository;
        this.tagRepository = tagRepository;
    }

    void processLinks(StickyNote stickyNote, boolean isAddNewStickyNote) {
        if (stickyNote == null) {
            throw new StickyNoteValidationException("Sticky note must not be null");
        }
        if (stickyNote.getLinks() == null) {
            return;
        }
        for (StickyNoteLink link : stickyNote.getLinks()) {
            if (link.getId() == null) { // new link
                link.setStickyNote(stickyNote);
            } else {
                if (isAddNewStickyNote) {
                    throw new StickyNoteValidationException("Sticky note id already exists");
                }
                StickyNoteLink linkFromDB = stickyNoteLinkRepository.findById(link.getId())
                        .orElseThrow(() -> new NoSuchStickyNoteException("Sticky note link does not exist for id " + link.getId()));
                if (!linkFromDB.getStickyNote().getId().equals(stickyNote.getId())) {
                    throw new StickyNoteValidationException("Sticky note id does not match");
                }
            }
        }

    }

    void processTags(StickyNote stickyNote, boolean isAddNewStickyNote) {
        if (stickyNote == null) {
            throw new StickyNoteValidationException("Sticky note must not be null");
        }
        if (stickyNote.getTags() == null) {
            return;
        }
        for (StickyNoteTag tag : stickyNote.getTags()) {
            if (tag.getId() == null) {
                tag.setStickyNote(stickyNote);
            } else {
                if (isAddNewStickyNote) {
                    throw new StickyNoteValidationException("Sticky note id already exists");
                }
                StickyNoteTag tagFromDB = stickyNoteTagRepository.findById(tag.getId())
                        .orElseThrow(() -> new NoSuchTagException("Sticky note tag id does not exist"));
                if (!tagFromDB.getStickyNote().getId().equals(stickyNote.getId())) {
                    throw new StickyNoteValidationException("Sticky note id does not match");
                }
            }
            if (tag.getTag() == null || tag.getTag().getId() == null) {
                throw new NoSuchTagException("Missing tag reference or tag id for sticky note tag " + tag);
            }
            var existingTagId = tag.getTag().getId();
            var existingTagFromDB = tagRepository.findById(existingTagId);
            tag.setTag(existingTagFromDB.orElseThrow(() -> new NoSuchTagException("No tag found for id " + existingTagId)));
        }
    }

}
