package org.softwarecave.springbootnote.tag.web;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.softwarecave.springbootnote.config.security.SecurityConfiguration;
import org.softwarecave.springbootnote.tag.model.NoSuchTagException;
import org.softwarecave.springbootnote.tag.model.Tag;
import org.softwarecave.springbootnote.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TagController.class)
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    @WithMockUser(roles = SecurityConfiguration.TAGS_ADMIN)
    public void testAddTag_NotExists() throws Exception {
        TagDTO tagDTO = new TagDTO(null, "JUnit", "JUnit test framework");

        var argCaptor = ArgumentCaptor.forClass(Tag.class);
        when(tagService.addTag(argCaptor.capture())).thenReturn(new Tag(123L, "JUnit", "JUnit test framework"));

        mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonMapper.writeValueAsString(tagDTO))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(123L))
                .andExpect(jsonPath("$.name").value("JUnit"))
                .andExpect(jsonPath("$.description").value("JUnit test framework"));

        verify(tagService).addTag(any(Tag.class));
        assertThat(argCaptor.getValue())
                .hasFieldOrPropertyWithValue("id", null)
                .hasFieldOrPropertyWithValue("name", "JUnit")
                .hasFieldOrPropertyWithValue("description", "JUnit test framework");
    }

    @Test
    @WithMockUser(roles = SecurityConfiguration.TAGS_ADMIN)
    public void testAddTag_ExistsAlready() throws Exception {
        TagDTO tagDTO = new TagDTO(null, "JUnit", "JUnit test framework");

        when(tagService.addTag(any())).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(post("/api/v1/tags", 123L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonMapper.writeValueAsString(tagDTO))
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(tagService).addTag(any(Tag.class));
    }

    @Test
    @WithMockUser(roles = SecurityConfiguration.TAGS_VIEWER)
    public void testGetTags_Exists() throws Exception {
        var tags = List.of(
                new Tag(512L, "JUnit", "JUnit test framework"),
                new Tag(51L, "Git", "Git SCM"));

        when(tagService.getTags()).thenReturn(tags);

        mockMvc.perform(get("/api/v1/tags")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].id").value(512L))
                .andExpect(jsonPath("$[0].name").value("JUnit"))
                .andExpect(jsonPath("$[0].description").value("JUnit test framework"))
                .andExpect(jsonPath("$[1].id").value(51L))
                .andExpect(jsonPath("$[1].name").value("Git"))
                .andExpect(jsonPath("$[1].description").value("Git SCM"));

        verify(tagService).getTags();
    }

    @Test
    @WithMockUser(roles = SecurityConfiguration.TAGS_VIEWER)
    public void testGetTag_Exists() throws Exception {
        Tag tag = new Tag(512L, "JUnit", "JUnit test framework");

        when(tagService.getTagById(512L)).thenReturn(tag);

        mockMvc.perform(get("/api/v1/tags/{id}", 512L)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(512L))
                .andExpect(jsonPath("$.name").value("JUnit"))
                .andExpect(jsonPath("$.description").value("JUnit test framework"));

        verify(tagService).getTagById(512L);
    }

    @Test
    @WithMockUser(roles = SecurityConfiguration.TAGS_VIEWER)
    public void testGetTag_NotExists() throws Exception {

        when(tagService.getTagById(512L)).thenThrow(NoSuchTagException.class);

        mockMvc.perform(get("/api/v1/tags/{id}", 512L)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());

        verify(tagService).getTagById(512L);
    }

    @Test
    @WithMockUser(roles = SecurityConfiguration.TAGS_ADMIN)
    public void testUpdateTag_Exists() throws Exception {
        TagDTO tagDTO = new TagDTO(123L, "Git", "Git SCM");

        var argCaptor = ArgumentCaptor.forClass(Tag.class);
        when(tagService.updateTag(argCaptor.capture())).thenReturn(new Tag(123L, "Git", "Git SCM"));

        mockMvc.perform(put("/api/v1/tags/{id}", 123L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonMapper.writeValueAsString(tagDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(123L))
                .andExpect(jsonPath("$.name").value("Git"))
                .andExpect(jsonPath("$.description").value("Git SCM"));

        verify(tagService).updateTag(any(Tag.class));
        assertThat(argCaptor.getValue())
                .hasFieldOrPropertyWithValue("id", 123L)
                .hasFieldOrPropertyWithValue("name", "Git")
                .hasFieldOrPropertyWithValue("description", "Git SCM");
    }

    @Test
    @WithMockUser(roles = SecurityConfiguration.TAGS_ADMIN)
    public void testUpdateTag_NotExists() throws Exception {
        TagDTO tagDTO = new TagDTO(123L, "Git", "Git SCM");

        var argCaptor = ArgumentCaptor.forClass(Tag.class);
        when(tagService.updateTag(argCaptor.capture())).thenThrow(NoSuchTagException.class);

        mockMvc.perform(put("/api/v1/tags/{id}", 123L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonMapper.writeValueAsString(tagDTO))
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(tagService).updateTag(any(Tag.class));
        assertThat(argCaptor.getValue())
                .hasFieldOrPropertyWithValue("id", 123L)
                .hasFieldOrPropertyWithValue("name", "Git")
                .hasFieldOrPropertyWithValue("description", "Git SCM");
    }

    @Test
    @WithMockUser(roles = SecurityConfiguration.TAGS_ADMIN)
    public void testUpdateTag_IdMismatchBetweenPathVariableAndDTO() throws Exception {
        TagDTO tagDTO = new TagDTO(123L, "Git", "Git SCM");

        mockMvc.perform(put("/api/v1/tags/{id}", 150L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonMapper.writeValueAsString(tagDTO))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = SecurityConfiguration.TAGS_ADMIN)
    public void testDeleteTag_Exists() throws Exception {
        doNothing().when(tagService).deleteTag(5L);

        mockMvc.perform(delete("/api/v1/tags/{id}", 5L)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(tagService).deleteTag(5L);
    }
}
