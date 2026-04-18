package org.softwarecave.springbootnote.tag.service;

import org.softwarecave.springbootnote.tag.model.NoSuchTagException;
import org.softwarecave.springbootnote.tag.model.Tag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    public Tag getTagById(Long id) {
        if (id == null) {
            throw new NoSuchTagException("No tag id provided");
        }
        return tagRepository.findById(id)
                .orElseThrow(() -> new NoSuchTagException("No tag found for ID " + id));
    }

    public Tag addTag(Tag tag) {
        if (tag.getId() != null) {
            throw new IllegalArgumentException("Tag already exists");
        }
        return tagRepository.save(tag);
    }

    public Tag updateTag(Tag tag) {
        if (tagRepository.existsById(tag.getId())) {
            return tagRepository.save(tag);
        } else {
            throw new NoSuchTagException("No tag found for ID " + tag.getId());
        }
    }

    public void deleteTag(Long id) {
        if (tagRepository.existsById(id)) {
            tagRepository.deleteById(id);
        } else {
            throw new NoSuchTagException("No tag found for ID " + id);
        }
    }

    public Optional<Tag> getTagByName(String name) {
        return tagRepository.findByName(name);
    }
}
