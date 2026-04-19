package org.softwarecave.springbootnote.tag.service;

import org.softwarecave.springbootnote.tag.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}
