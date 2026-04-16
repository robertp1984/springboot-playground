package org.softwarecave.springbootnote.note.service;

import org.softwarecave.springbootnote.note.model.StickyNoteTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StickyNoteTagRepository extends JpaRepository<StickyNoteTag, Long> {
}
