package org.softwarecave.springbootnote.note;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.softwarecave.springbootnote.note.service.StickyNoteService;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.model.StickyNoteLink;
import org.softwarecave.springbootnote.note.model.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public static final List<String> FOUR_URLS = Arrays.asList(URL1, URL2, URL3, URL4);


    @Autowired
    private StickyNoteService stickyNoteService;

    @Test
    public void addStickyNoteNoLinks_Valid() {
        var stickyNote = createStickyNoteWithoutLinks();

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
    public void addStickyNoteNoLinks_PopulatedId() {
        var stickyNote = createStickyNoteWithoutLinks();
        stickyNote.setId(5L);
        assertThrows(IllegalArgumentException.class, () -> stickyNoteService.addStickyNote(stickyNote));
    }

    @Test
    public void addStickyNoteNoLinks_NullTitle() {
        var stickyNote = createStickyNoteWithoutLinks();
        stickyNote.setTitle(null);
        var e = assertThrows(Exception.class, () -> stickyNoteService.addStickyNote(stickyNote));
        assertIsConstraintViolationException(e);
    }


    @Test
    public void addStickyNoteNoLinks_EmptyTitle() {
        var stickyNote = createStickyNoteWithoutLinks();
        stickyNote.setTitle("");
        var e = assertThrows(Exception.class, () -> stickyNoteService.addStickyNote(stickyNote));
        assertIsConstraintViolationException(e);
    }

    @Test
    public void addStickyNoteNoLinks_NullBody() {
        var stickyNote = createStickyNoteWithoutLinks();
        stickyNote.setBody("");
        var e = assertThrows(Exception.class, () -> stickyNoteService.addStickyNote(stickyNote));
        assertIsConstraintViolationException(e);
    }

    @Test
    public void addStickyNoteNoLinks_EmptyBody() {
        var stickyNote = createStickyNoteWithoutLinks();
        stickyNote.setBody("");
        var e = assertThrows(Exception.class, () -> stickyNoteService.addStickyNote(stickyNote));
        assertIsConstraintViolationException(e);
    }

    @Test
    public void addStickyNoteNoLinks_NullType() {
        var stickyNote = createStickyNoteWithoutLinks();
        stickyNote.setType(null);
        var e = assertThrows(Exception.class, () -> stickyNoteService.addStickyNote(stickyNote));
        assertIsConstraintViolationException(e);
    }

    @Test
    public void addStickyNoteNoLinks_NullDate() {
        var stickyNote = createStickyNoteWithoutLinks();
        stickyNote.setCreated(null);
        var e = assertThrows(Exception.class, () -> stickyNoteService.addStickyNote(stickyNote));
        assertIsConstraintViolationException(e);
    }

    @Test
    public void addStickyNote2Links_Valid() {
        var stickyNote = createStickyNoteWith2Links();

        var savedStickyNote = stickyNoteService.addStickyNote(stickyNote);

        assertThat(savedStickyNote)
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", "My title")
                .hasFieldOrPropertyWithValue("body", "My body")
                .hasFieldOrPropertyWithValue("type", Type.PLAIN_TEXT);
        assertThat(savedStickyNote.getId()).isGreaterThan(0);

        assertThat(savedStickyNote.getLinks()).hasSize(2);

        StickyNoteLink savedLink1 = savedStickyNote.getLinks().get(0);
        assertThat(savedLink1.getId()).isGreaterThan(0);
        assertThat(savedLink1.getLink()).isEqualTo(URL1);

        StickyNoteLink savedLink2 = savedStickyNote.getLinks().get(1);
        assertThat(savedLink2.getId()).isGreaterThan(0);
        assertThat(savedLink2.getLink()).isEqualTo(URL2);
    }

    @Test
    public void updateStickyNote2Links_Valid_Add2NewLinks() {
        var stickyNote = createStickyNoteWith2Links();
        var addedStickyNote = stickyNoteService.addStickyNote(stickyNote);

        var link3 = new StickyNoteLink(null, addedStickyNote, URL3);
        var link4 = new StickyNoteLink(null, addedStickyNote, URL4);
        addedStickyNote.getLinks().add(link3);
        addedStickyNote.getLinks().add(link4);


        var updatedStickyNote = stickyNoteService.updateStickyNote(addedStickyNote);

        assertThat(updatedStickyNote.getId()).isGreaterThan(0);
        assertThat(updatedStickyNote.getTitle()).isEqualTo("My title");

        var links = updatedStickyNote.getLinks();
        assertThat(links).hasSize(4);
        assertTrue(links.stream().allMatch(link -> link.getId() > 0));
        assertTrue(links.stream().allMatch(link -> link.getStickyNote().getId().equals(updatedStickyNote.getId())));
        assertTrue(links.stream().map(StickyNoteLink::getLink).toList().containsAll(FOUR_URLS));
    }

    @Test
    public void updateStickyNote2Links_Valid_ModifyOneLink() {
        var stickyNote = createStickyNoteWith2Links();
        var addedStickyNote = stickyNoteService.addStickyNote(stickyNote);
        addedStickyNote.getLinks().getFirst().setLink(URL3);

        var updatedStickyNote = stickyNoteService.updateStickyNote(addedStickyNote);

        assertThat(updatedStickyNote.getId()).isGreaterThan(0);
        assertThat(updatedStickyNote.getTitle()).isEqualTo("My title");

        var links = updatedStickyNote.getLinks();
        assertThat(links).hasSize(2);
        assertTrue(links.stream().allMatch(link -> link.getId() > 0));
        assertTrue(links.stream().allMatch(link -> link.getStickyNote().getId().equals(updatedStickyNote.getId())));
        assertTrue(links.stream().map(StickyNoteLink::getLink).toList().containsAll(List.of(URL3, URL2)));
    }

    @Test
    public void updateStickyNote2Links_Valid_AddOneLink() {
        var stickyNote = createStickyNoteWith2Links();
        var addedStickyNote = stickyNoteService.addStickyNote(stickyNote);
        var link3 = new StickyNoteLink(null, addedStickyNote, URL3);
        stickyNote.getLinks().add(link3);

        var updatedStickyNote = stickyNoteService.updateStickyNote(addedStickyNote);

        assertThat(updatedStickyNote.getId()).isGreaterThan(0);
        assertThat(updatedStickyNote.getTitle()).isEqualTo("My title");

        var links = updatedStickyNote.getLinks();
        assertThat(links).hasSize(3);
        assertTrue(links.stream().allMatch(link -> link.getId() > 0));
        assertTrue(links.stream().allMatch(link -> link.getStickyNote().getId().equals(updatedStickyNote.getId())));
        assertTrue(links.stream().map(StickyNoteLink::getLink).toList().containsAll(List.of(URL1, URL2, URL3)));
    }

    @Test
    public void getStickyNote2Links_Valid() {
        var stickyNote1 = createStickyNoteWith2Links();
        stickyNote1.setTitle("Note1");
        var stickyNote2 = createStickyNoteWith2Links();
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
    }

    @Test
    public void getAllStickyNote2Links_Valid() {
        var stickyNote1 = createStickyNoteWith2Links();
        stickyNote1.setTitle("Note1");
        var stickyNote2 = createStickyNoteWith2Links();
        stickyNote2.setTitle("Note2");
        stickyNoteService.addStickyNote(stickyNote1);
        stickyNoteService.addStickyNote(stickyNote2);

        var stickyNotes = stickyNoteService.getStickyNotes();
        assertTrue(stickyNotes.stream().allMatch(stickyNote -> stickyNote.getId() > 0));
        var allTitles = List.of(stickyNote1.getTitle(), stickyNote2.getTitle());
        assertTrue(stickyNotes.stream().map(StickyNote::getTitle).toList().containsAll(allTitles));
    }

    private static StickyNote createStickyNoteWith2Links() {
        var stickyNote = createStickyNoteWithoutLinks();
        var link1 = new StickyNoteLink(null, stickyNote, URL1);
        var link2 = new StickyNoteLink(null, stickyNote, URL2);
        stickyNote.getLinks().add(link1);
        stickyNote.getLinks().add(link2);
        return stickyNote;
    }

    private static StickyNote createStickyNoteWithoutLinks() {
        return new StickyNote(null, "My title", "My body", Type.PLAIN_TEXT, new ArrayList<>(), LocalDateTime.now());
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


}
