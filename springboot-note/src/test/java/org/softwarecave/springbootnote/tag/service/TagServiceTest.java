package org.softwarecave.springbootnote.tag.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.softwarecave.springbootnote.tag.model.NoSuchTagException;
import org.softwarecave.springbootnote.tag.model.Tag;
import org.softwarecave.springbootnote.tag.model.TagValidationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @Test
    void getTags_returnsAllTags() {
        var tags = List.of(new Tag(1L, "a", "A"), new Tag(2L, "b", "B"));
        when(tagRepository.findAll()).thenReturn(tags);

        var result = tagService.getTags();

        assertEquals(2, result.size());
        assertEquals(tags, result);
        verify(tagRepository).findAll();
    }

    @Test
    void getTagById_nullId_throws() {
        assertThrows(TagValidationException.class, () -> tagService.getTagById(null));
    }

    @Test
    void getTagById_notFound_throws() {
        when(tagRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NoSuchTagException.class, () -> tagService.getTagById(10L));
        verify(tagRepository).findById(10L);
    }

    @Test
    void getTagById_found_returnsTag() {
        var tag = new Tag(5L, "x", "X");
        when(tagRepository.findById(5L)).thenReturn(Optional.of(tag));

        var result = tagService.getTagById(5L);

        assertEquals(tag, result);
        verify(tagRepository).findById(5L);
    }

    @Test
    void addTag_withId_throws() {
        var tag = new Tag(1L, "n", "d");

        assertThrows(TagValidationException.class, () -> tagService.addTag(tag));
        verify(tagRepository, never()).save(any());
    }

    @Test
    void addTag_savesAndReturns() {
        var tag = new Tag(null, "n", "d");
        var saved = new Tag(11L, "n", "d");
        when(tagRepository.save(any(Tag.class))).thenReturn(saved);

        var result = tagService.addTag(tag);

        assertEquals(saved, result);
        verify(tagRepository).save(tag);
    }

    @Test
    void updateTag_exists_savesAndReturns() {
        var tag = new Tag(7L, "u", "ud");
        when(tagRepository.findById(7L)).thenReturn(Optional.of(tag));
        when(tagRepository.save(tag)).thenReturn(tag);

        var result = tagService.updateTag(tag);

        assertEquals(tag, result);
        verify(tagRepository).findById(7L);
        verify(tagRepository).save(tag);
    }

    @Test
    void updateTag_notExists_throws() {
        var tag = new Tag(8L, "u", "ud");
        when(tagRepository.findById(8L)).thenReturn(Optional.empty());

        assertThrows(NoSuchTagException.class, () -> tagService.updateTag(tag));
        verify(tagRepository).findById(8L);
        verify(tagRepository, never()).save(any());
    }

    @Test
    void deleteTag_exists_deletes() {
        Tag tag = new Tag(3L, "aa", "bb");
        when(tagRepository.findById(3L)).thenReturn(Optional.of(tag));
        doNothing().when(tagRepository).delete(tag);

        assertDoesNotThrow(() -> tagService.deleteTag(3L));
        verify(tagRepository).findById(3L);
        verify(tagRepository).delete(tag);
    }

    @Test
    void deleteTag_notExists_throws() {
        when(tagRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(NoSuchTagException.class, () -> tagService.deleteTag(4L));
        verify(tagRepository).findById(4L);
        verify(tagRepository, never()).deleteById(anyLong());
    }
}

