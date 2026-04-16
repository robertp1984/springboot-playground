package org.softwarecave.springbootnote.tag.service;

import org.softwarecave.springbootnote.tag.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
