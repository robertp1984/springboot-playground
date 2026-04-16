package org.softwarecave.springbootnote.note.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.softwarecave.springbootnote.tag.model.Tag;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sticky_note_tag")
public class StickyNoteTag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sticky_note_tag_seq")
    @SequenceGenerator(name = "sticky_note_tag_seq", sequenceName = "sticky_note_tag_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sticky_note_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotNull
    private StickyNote stickyNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    @EqualsAndHashCode.Exclude
    @NotNull
    private Tag tag;
}
