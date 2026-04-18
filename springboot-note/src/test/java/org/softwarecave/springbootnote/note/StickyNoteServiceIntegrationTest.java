package org.softwarecave.springbootnote.note;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.model.StickyNoteLink;
import org.softwarecave.springbootnote.note.model.StickyNoteTag;
import org.softwarecave.springbootnote.note.model.Type;
import org.softwarecave.springbootnote.note.service.StickyNoteService;
import org.softwarecave.springbootnote.tag.model.Tag;
import org.softwarecave.springbootnote.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This is an integration test for StickyNoteService.
 * The test uses H2 in-memory database.
 */
@SpringBootTest
public class StickyNoteServiceIntegrationTest {

    public static final String URL1 = "http://google.com";
    public static final String URL2 = "http://oracle.com";
    public static final String URL3 = "http://google.pl";
    public static final String URL4 = "http://interia.pl";
    public static final List<String> FOUR_URLS = List.of(URL1, URL2, URL3, URL4);

    public static final String TAG1 = "Tag1";
    public static final String TAG2 = "Tag2";
    public static final String TAG3 = "Tag3";

    @Autowired
    private StickyNoteService stickyNoteService;

    @Autowired
    private TagService tagService;

    @Test
    public void addStickyNote_Valid() {
        var stickyNote = createStickyNoteWithoutLinksAndTags();

        var savedStickyNote = stickyNoteService.addStickyNote(stickyNote);

        assertThat(savedStickyNote)
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", "My title")
                .hasFieldOrPropertyWithValue("body", "My body")
                .hasFieldOrPropertyWithValue("type", Type.PLAIN_TEXT);
        assertThat(savedStickyNote.getId()).isGreaterThan(0);
        assertThat(savedStickyNote.getLinks()).isEmpty();
    }

    @Test
    public void addStickyNote_PopulatedId() {
        var stickyNote = createStickyNoteWithoutLinksAndTags();
        stickyNote.setId(5L);
        assertThrows(IllegalArgumentException.class, () -> stickyNoteService.addStickyNote(stickyNote));
    }

    @Test
    public void addStickyNote_NullTitle() {
        var stickyNote = createStickyNoteWithoutLinksAndTags();
        stickyNote.setTitle(null);
        var e = assertThrows(Exception.class, () -> stickyNoteService.addStickyNote(stickyNote));
        assertIsConstraintViolationException(e);
    }


    @Test
    public void addStickyNote_EmptyTitle() {
        var stickyNote = createStickyNoteWithoutLinksAndTags();
        stickyNote.setTitle("");
        var e = assertThrows(Exception.class, () -> stickyNoteService.addStickyNote(stickyNote));
        assertIsConstraintViolationException(e);
    }

    @Test
    public void addStickyNote_NullBody() {
        var stickyNote = createStickyNoteWithoutLinksAndTags();
        stickyNote.setBody("");
        var e = assertThrows(Exception.class, () -> stickyNoteService.addStickyNote(stickyNote));
        assertIsConstraintViolationException(e);
    }

    @Test
    public void addStickyNote_EmptyBody() {
        var stickyNote = createStickyNoteWithoutLinksAndTags();
        stickyNote.setBody("");
        var e = assertThrows(Exception.class, () -> stickyNoteService.addStickyNote(stickyNote));
        assertIsConstraintViolationException(e);
    }

    @Test
    public void addStickyNote_NullType() {
        var stickyNote = createStickyNoteWithoutLinksAndTags();
        stickyNote.setType(null);
        var e = assertThrows(Exception.class, () -> stickyNoteService.addStickyNote(stickyNote));
        assertIsConstraintViolationException(e);
    }

    @Test
    public void addStickyNote_NullDate() {
        var stickyNote = createStickyNoteWithoutLinksAndTags();
        stickyNote.setCreated(null);
        var e = assertThrows(Exception.class, () -> stickyNoteService.addStickyNote(stickyNote));
        assertIsConstraintViolationException(e);
    }

    @Test
    public void addStickyNote2LinksAnd2Tags_Valid() {
        var stickyNote = createStickyNoteWith2LinksAndTags();

        var savedStickyNote = stickyNoteService.addStickyNote(stickyNote);

        assertThat(savedStickyNote)
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", "My title")
                .hasFieldOrPropertyWithValue("body", "My body")
                .hasFieldOrPropertyWithValue("type", Type.PLAIN_TEXT);
        assertThat(savedStickyNote.getId()).isGreaterThan(0);

        // verify links
        assertThat(savedStickyNote.getLinks()).hasSize(2);
        assertThat(savedStickyNote.getLinks()).extracting(StickyNoteLink::getId)
                .allMatch(id -> id > 0);
        assertThat(savedStickyNote.getLinks()).extracting(StickyNoteLink::getLink)
                .containsAll(List.of(URL1, URL2));

        // verify tags
        assertThat(savedStickyNote.getTags()).hasSize(2);
        assertThat(savedStickyNote.getLinks()).extracting(StickyNoteLink::getId)
                .allMatch(id -> id > 0);
        assertThat(savedStickyNote.getTags()).extracting(t -> t.getTag().getName())
                .containsAll(List.of(TAG1, TAG2));
    }

    @Test
    public void updateStickyNote2Links_Valid_Add2NewLinksAnd1Tag() {
        var stickyNote = createStickyNoteWith2LinksAndTags();
        var addedStickyNote = stickyNoteService.addStickyNote(stickyNote);

        var link3 = new StickyNoteLink(null, addedStickyNote, URL3);
        var link4 = new StickyNoteLink(null, addedStickyNote, URL4);
        addedStickyNote.getLinks().add(link3);
        addedStickyNote.getLinks().add(link4);

        var tag3 = new StickyNoteTag(null, addedStickyNote, createOrGetTag(TAG3, "Tag3 description"));
        addedStickyNote.getTags().add(tag3);

        var updatedStickyNote = stickyNoteService.updateStickyNote(addedStickyNote);

        assertThat(updatedStickyNote.getId()).isGreaterThan(0);
        assertThat(updatedStickyNote.getTitle()).isEqualTo("My title");

        var links = updatedStickyNote.getLinks();
        assertThat(links).hasSize(4);
        assertTrue(links.stream().allMatch(link -> link.getId() > 0));
        assertTrue(links.stream().allMatch(link -> link.getStickyNote().getId().equals(updatedStickyNote.getId())));
        assertThat(links.stream().map(StickyNoteLink::getLink).toList())
                .containsAll(FOUR_URLS);

        var tags = updatedStickyNote.getTags();
        assertThat(tags).hasSize(3);
        assertTrue(tags.stream().allMatch(tag -> tag.getId() > 0));
        assertTrue(tags.stream().allMatch(tag -> tag.getStickyNote().getId().equals(updatedStickyNote.getId())));
        assertThat(tags.stream().map(t -> t.getTag().getName()).toList())
                .containsAll(List.of(TAG1, TAG2, TAG3));
    }

    @Test
    public void updateStickyNote2Links_Valid_ModifyOneLinkAndOneTag() {
        var stickyNote = createStickyNoteWith2LinksAndTags();
        var addedStickyNote = stickyNoteService.addStickyNote(stickyNote);
        addedStickyNote.getLinks().getFirst().setLink(URL3);
        addedStickyNote.getTags().getFirst().setTag(createOrGetTag(TAG3, "Tag3 description"));

        var updatedStickyNote = stickyNoteService.updateStickyNote(addedStickyNote);

        assertThat(updatedStickyNote.getId()).isGreaterThan(0);
        assertThat(updatedStickyNote.getTitle()).isEqualTo("My title");

        var links = updatedStickyNote.getLinks();
        assertThat(links).hasSize(2);
        assertTrue(links.stream().allMatch(link -> link.getId() > 0));
        assertTrue(links.stream().allMatch(link -> link.getStickyNote().getId().equals(updatedStickyNote.getId())));
        assertThat(links.stream().map(StickyNoteLink::getLink).toList())
                .containsAll(List.of(URL3, URL2));

        var tags = updatedStickyNote.getTags();
        assertThat(tags).hasSize(2);
        assertTrue(tags.stream().allMatch(tag -> tag.getId() > 0));
        assertTrue(tags.stream().allMatch(tag -> tag.getStickyNote().getId().equals(updatedStickyNote.getId())));
        assertThat(tags.stream().map(t -> t.getTag().getName()).toList())
                .containsAll(List.of(TAG3, TAG2));
    }

    @Test
    public void updateStickyNote2Links_Valid_AddOneLinkAndOneTag() {
        var stickyNote = createStickyNoteWith2LinksAndTags();
        var addedStickyNote = stickyNoteService.addStickyNote(stickyNote);
        var link3 = new StickyNoteLink(null, addedStickyNote, URL3);
        stickyNote.getLinks().add(link3);
        var tag3 = new StickyNoteTag(null, addedStickyNote, createOrGetTag(TAG3, "Tag3 description"));
        addedStickyNote.getTags().add(tag3);

        var updatedStickyNote = stickyNoteService.updateStickyNote(addedStickyNote);

        assertThat(updatedStickyNote.getId()).isGreaterThan(0);
        assertThat(updatedStickyNote.getTitle()).isEqualTo("My title");

        var links = updatedStickyNote.getLinks();
        assertThat(links).hasSize(3);
        assertTrue(links.stream().allMatch(link -> link.getId() > 0));
        assertTrue(links.stream().allMatch(link -> link.getStickyNote().getId().equals(updatedStickyNote.getId())));
        assertThat(links.stream().map(StickyNoteLink::getLink).toList())
                .containsAll(List.of(URL1, URL2, URL3));

        var tags = updatedStickyNote.getTags();
        assertThat(links).hasSize(3);
        assertTrue(tags.stream().allMatch(tag -> tag.getId() > 0));
        assertTrue(tags.stream().allMatch(tag -> tag.getStickyNote().getId().equals(updatedStickyNote.getId())));
        assertThat(tags.stream().map(t -> t.getTag().getName()).toList())
                .containsAll(List.of(TAG1, TAG2, TAG3));
    }

    @Test
    public void getStickyNote2LinksAndTags_Valid() {
        var stickyNote1 = createStickyNoteWith2LinksAndTags();
        stickyNote1.setTitle("Note1");
        var stickyNote2 = createStickyNoteWith2LinksAndTags();
        stickyNote2.setTitle("Note2");
        @SuppressWarnings("unused")
        var addedStickyNote1 = stickyNoteService.addStickyNote(stickyNote1);
        var addedStickyNote2 = stickyNoteService.addStickyNote(stickyNote2);

        // get sticky note 2 and check
        var gotStickyNote2 = stickyNoteService.getStickyNoteById(addedStickyNote2.getId());
        assertThat(gotStickyNote2.getTitle()).isEqualTo("Note2");
        var links = gotStickyNote2.getLinks();
        assertThat(links).hasSize(2);
        assertTrue(links.stream().allMatch(link -> link.getId() > 0));
        var tags = gotStickyNote2.getTags();
        assertThat(tags).hasSize(2);
        assertTrue(tags.stream().allMatch(tag -> tag.getId() > 0));
    }

    @Test
    public void getAllStickyNote2Links_Valid() {
        var stickyNote1 = createStickyNoteWith2LinksAndTags();
        stickyNote1.setTitle("Note1");
        var stickyNote2 = createStickyNoteWith2LinksAndTags();
        stickyNote2.setTitle("Note2");
        stickyNoteService.addStickyNote(stickyNote1);
        stickyNoteService.addStickyNote(stickyNote2);

        var stickyNotes = stickyNoteService.getStickyNotes();
        assertTrue(stickyNotes.stream().allMatch(stickyNote -> stickyNote.getId() > 0));
        var allTitles = List.of(stickyNote1.getTitle(), stickyNote2.getTitle());
        assertTrue(stickyNotes.stream().map(StickyNote::getTitle).toList().containsAll(allTitles));
    }

    private StickyNote createStickyNoteWith2LinksAndTags() {
        var stickyNote = createStickyNoteWithoutLinksAndTags();

        var link1 = new StickyNoteLink(null, stickyNote, URL1);
        var link2 = new StickyNoteLink(null, stickyNote, URL2);
        stickyNote.getLinks().add(link1);
        stickyNote.getLinks().add(link2);

        var tag1 = new StickyNoteTag(null, stickyNote, createOrGetTag(TAG1, "Tag1 description"));
        var tag2 = new StickyNoteTag(null, stickyNote, createOrGetTag(TAG2, "Tag2 description"));
        stickyNote.getTags().add(tag1);
        stickyNote.getTags().add(tag2);

        return stickyNote;
    }

    private static StickyNote createStickyNoteWithoutLinksAndTags() {
        return new StickyNote(null, "My title", "My body", Type.PLAIN_TEXT, new ArrayList<>(), new ArrayList<>(), LocalDateTime.now());
    }

    private void assertIsConstraintViolationException(Throwable exception) {
        var cause = exception;
        while (cause != null) {
            if (cause instanceof ConstraintViolationException) {
                return;
            }
            cause = cause.getCause();
        }
        assertFalse(false, "ConstraintViolationException expected but not thrown");
    }

    private Tag createOrGetTag(String name, String description) {
        var tagByName = tagService.getTagByName(name);
        if (tagByName.isPresent()) {
            return tagByName.get();
        } else {
            var tag = new Tag(null, name, description);
            return tagService.addTag(tag);
        }
    }
}
