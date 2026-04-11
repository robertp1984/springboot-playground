package org.softwarecave.springbootnote.note.service;

import org.softwarecave.springbootnote.note.model.StickyNoteLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StickyNoteLinkRepository extends JpaRepository<StickyNoteLink, Long> {
}
