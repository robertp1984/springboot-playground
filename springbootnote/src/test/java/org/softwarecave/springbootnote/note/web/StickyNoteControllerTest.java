package org.softwarecave.springbootnote.note.web;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.softwarecave.springbootnote.config.security.SecurityConfiguration;
import org.softwarecave.springbootnote.note.model.NoSuchStickyNoteException;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.model.Type;
import org.softwarecave.springbootnote.note.service.StickyNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {StickyNoteController.class})
public class StickyNoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StickyNoteService stickyNoteService;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    @WithMockUser(roles = SecurityConfiguration.STICKY_NOTES_VIEWER)
    public void testGetStickyNote() throws Exception {
        StickyNote stickyNote = new StickyNote(6L, "CSV note", "CSV note body", Type.PLAIN_TEXT, null, LocalDateTime.now());
        when(stickyNoteService.getStickyNoteById(6L)).thenReturn(stickyNote);

        mockMvc.perform(get("/api/v1/stickyNotes/6"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value("6"))
                .andExpect(jsonPath("$.title").value("CSV note"))
                .andExpect(jsonPath("$.body").value("CSV note body"))
                .andExpect(jsonPath("$.type").value("PLAIN_TEXT"))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = SecurityConfiguration.STICKY_NOTES_VIEWER)
    public void testGetStickyNotes() throws Exception {
        StickyNote stickyNote1 = new StickyNote(1L, "CSV note1", "CSV note body1", Type.PLAIN_TEXT, null, LocalDateTime.now());
        StickyNote stickyNote2 = new StickyNote(2L, "CSV note2", "CSV note body2", Type.PLAIN_TEXT, null, LocalDateTime.now());
        when(stickyNoteService.getStickyNotes()).thenReturn(List.of(stickyNote1, stickyNote2));

        mockMvc.perform(get("/api/v1/stickyNotes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].title").value("CSV note1"))
                .andExpect(jsonPath("$[0].body").value("CSV note body1"))
                .andExpect(jsonPath("$[0].type").value("PLAIN_TEXT"))
                .andExpect(jsonPath("$[0].created").isNotEmpty())
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].title").value("CSV note2"))
                .andExpect(jsonPath("$[1].body").value("CSV note body2"))
                .andExpect(jsonPath("$[1].type").value("PLAIN_TEXT"))
                .andExpect(jsonPath("$[1].created").isNotEmpty());

    }

    @Test
    @WithMockUser(roles = SecurityConfiguration.STICKY_NOTES_VIEWER)
    public void testGetStickyNote_NotFound() throws Exception {
        when(stickyNoteService.getStickyNoteById(2L))
                .thenThrow(new NoSuchStickyNoteException("No sticky note found for ID 2"));

        mockMvc.perform(get("/api/v1/stickyNotes/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = SecurityConfiguration.STICKY_NOTES_MANAGER)
    public void addStickyNote() throws Exception {
        StickyNoteDTO stickyNoteDTO = new StickyNoteDTO(null, "Title", "Body", Type.PLAIN_TEXT, List.of(), LocalDateTime.now());
        StickyNote addedStickyNote = new StickyNote(5L, "Title", "Body", Type.PLAIN_TEXT, List.of(), LocalDateTime.now());

        var argumentCaptor = ArgumentCaptor.forClass(StickyNote.class);
        when(stickyNoteService.addStickyNote(argumentCaptor.capture())).thenReturn(addedStickyNote);

        mockMvc.perform(post("/api/v1/stickyNotes")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonMapper.writeValueAsString(stickyNoteDTO))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(addedStickyNote.getId().toString()))
                .andExpect(jsonPath("$.title").value(addedStickyNote.getTitle()))
                .andExpect(jsonPath("$.body").value(addedStickyNote.getBody()))
                .andExpect(jsonPath("$.type").value(addedStickyNote.getType().name()))
                .andExpect(jsonPath("$.links").isEmpty())
                .andExpect(jsonPath("$.created").isNotEmpty());

        assertThat(argumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("title", stickyNoteDTO.getTitle())
                .hasFieldOrPropertyWithValue("body", stickyNoteDTO.getBody())
                .hasFieldOrPropertyWithValue("type", stickyNoteDTO.getType())
                .hasFieldOrPropertyWithValue("links", List.of())
                .hasFieldOrPropertyWithValue("created", stickyNoteDTO.getCreated());
    }

    @Test
    @WithMockUser(roles = SecurityConfiguration.STICKY_NOTES_MANAGER)
    public void updateStickyNote() throws Exception {
        StickyNoteDTO stickyNoteDTO = new StickyNoteDTO(5L, "Title", "Body", Type.PLAIN_TEXT, List.of(), LocalDateTime.now());
        StickyNote updatedStickyNote = new StickyNote(5L, "Title", "Body", Type.PLAIN_TEXT, List.of(), LocalDateTime.now());

        when(stickyNoteService.updateStickyNote(any())).thenReturn(updatedStickyNote);

        mockMvc.perform(put("/api/v1/stickyNotes/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonMapper.writeValueAsString(stickyNoteDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(updatedStickyNote.getId().toString()))
                .andExpect(jsonPath("$.title").value(updatedStickyNote.getTitle()))
                .andExpect(jsonPath("$.body").value(updatedStickyNote.getBody()))
                .andExpect(jsonPath("$.type").value(updatedStickyNote.getType().name()))
                .andExpect(jsonPath("$.links").isEmpty())
                .andExpect(jsonPath("$.created").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = SecurityConfiguration.STICKY_NOTES_ADMIN)
    public void deleteStickyNote() throws Exception {
        doNothing().when(stickyNoteService).deleteStickyNote(5L);

        mockMvc.perform(delete("/api/v1/stickyNotes/{id}", 5)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(stickyNoteService).deleteStickyNote(5L);
    }
}
