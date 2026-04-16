package org.softwarecave.springbootnote.tag.web;

import jakarta.validation.Valid;
import org.softwarecave.springbootnote.tag.model.NoSuchTagException;
import org.softwarecave.springbootnote.tag.model.Tag;
import org.softwarecave.springbootnote.tag.service.TagRepository;
import org.softwarecave.springbootnote.tag.service.TagService;
import org.softwarecave.springbootnote.tag.web.converter.TagConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;
    private final TagConverter tagConverter;

    public TagController(TagService tagService) {
        this.tagService = tagService;
        this.tagConverter = new TagConverter();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TagDTO> getTags() {
        var tags = tagService.getTags();
        return tags.stream().map(tagConverter::convertToDTO).toList();
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TagDTO getTag(@PathVariable("id") Long id) {
        var tag = tagService.getTagById(id);
        return tagConverter.convertToDTO(tag);
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TagDTO> createTag(@Valid @RequestBody TagDTO tagDTO) {
        var tag = tagConverter.convertToEntity(tagDTO);
        var addedTag = tagService.addTag(tag);

        var addedTagDTO = tagConverter.convertToDTO(addedTag);

        URI locationUri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}").buildAndExpand(addedTag.getId()).toUri();
        return ResponseEntity.created(locationUri).body(addedTagDTO);
    }

    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TagDTO updateTag(@PathVariable("id") Long id, @Valid @RequestBody TagDTO tagDTO) {
        if (tagDTO.getId() != null && !tagDTO.getId().equals(id)) {
            throw new IllegalArgumentException("Tag IDs are not equal");
        }
        tagDTO.setId(id);

        var tag = tagConverter.convertToEntity(tagDTO);

        Tag updatedTag = tagService.updateTag(tag);
        return tagConverter.convertToDTO(updatedTag);
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable("id") Long id) {
        tagService.deleteTag(id);
    }
}
