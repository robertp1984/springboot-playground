package org.softwarecave.springbootnote.note.web;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StickyNoteLinkDTO {
    @EqualsAndHashCode.Exclude
    private Long id;

    @EqualsAndHashCode.Exclude
    private Long stickyNoteId;

    @NotNull
    @NotEmpty
    private String link;
}
