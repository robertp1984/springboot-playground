package org.softwarecave.springbootnote.note.web;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.softwarecave.springbootnote.tag.web.TagDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StickyNoteTagDTO {

    @EqualsAndHashCode.Exclude
    private Long id;

    @EqualsAndHashCode.Exclude
    private Long stickyNoteId;

    @NotNull
    private TagDTO tag;
}
