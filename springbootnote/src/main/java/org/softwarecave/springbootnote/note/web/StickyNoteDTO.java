package org.softwarecave.springbootnote.note.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.softwarecave.springbootnote.note.model.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StickyNoteDTO {
    @EqualsAndHashCode.Exclude
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    @NotNull
    private Type type;

    private List<StickyNoteLinkDTO> links = new ArrayList<>();

    @PastOrPresent
    @NotNull
    private LocalDateTime created;
}
