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

@Service
@Transactional
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
                        .orElseThrow(() -> new NoSuchStickyNoteException("No sticky note found for id " + link.getId()));
                if (!linkFromDB.getStickyNote().getId().equals(stickyNote.getId())) {
                    throw new StickyNoteValidationException("Sticky note id does not match");
                }
            }
        }

    }

    void processTags(StickyNote stickyNote, boolean isAddNewStickyNote) {
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
            var existingTagId = tag.getTag().getId();
            if (existingTagId == null) {
                throw new NoSuchTagException("No tag found for id " + existingTagId);
            }
            var existingTagFromDB = tagRepository.findById(existingTagId);
            tag.setTag(existingTagFromDB.orElseThrow(() -> new NoSuchTagException("No tag found for id " + existingTagId)));
        }
    }

}
