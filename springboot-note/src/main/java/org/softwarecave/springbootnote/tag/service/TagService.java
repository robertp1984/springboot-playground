package org.softwarecave.springbootnote.tag.service;

import org.softwarecave.springbootnote.tag.model.NoSuchTagException;
import org.softwarecave.springbootnote.tag.model.Tag;
import org.softwarecave.springbootnote.tag.model.TagValidationException;
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

    @Transactional(readOnly = true)
    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Tag getTagById(Long id) {
        if (id == null) {
            throw new TagValidationException("No tag id provided");
        }
        return tagRepository.findById(id)
                .orElseThrow(() -> new NoSuchTagException("No tag found for ID " + id));
    }

    public Tag addTag(Tag tag) {
        if (tag == null) {
            throw new TagValidationException("Tag is null");
        }
        if (tag.getId() != null) {
            throw new TagValidationException("New tag already contains id");
        }
        return tagRepository.save(tag);
    }

    public Tag updateTag(Tag tag) {
        if (tag == null) {
            throw new TagValidationException("Tag is null");
        }
        if (tag.getId() == null) {
            throw new TagValidationException("No tag id provided");
        }
        Tag existingTag = tagRepository.findById(tag.getId())
                .orElseThrow(() -> new NoSuchTagException("No tag found for ID " + tag.getId()));
        existingTag.setName(tag.getName());
        existingTag.setDescription(tag.getDescription());

        return tagRepository.save(existingTag);
    }

    public void deleteTag(Long id) {
        if (id == null) {
            throw new TagValidationException("No tag id provided");
        }
        Optional<Tag> foundTag = tagRepository.findById(id);
        tagRepository.delete(foundTag.orElseThrow(() -> new NoSuchTagException("No tag found for ID " + id)));
    }

    @Transactional(readOnly = true)
    public Optional<Tag> getTagByName(String name) {
        if (name == null) {
            throw new TagValidationException("Tag name is null");
        }
        return tagRepository.findByName(name);
    }
}
