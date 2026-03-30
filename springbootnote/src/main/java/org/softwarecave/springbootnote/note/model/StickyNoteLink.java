package org.softwarecave.springbootnote.note.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sticky_note_link")
public class StickyNoteLink {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "sticky_note_link_seq", sequenceName = "sticky_note_link_seq",  allocationSize = 1)
    @Column(name = "id")
    @EqualsAndHashCode.Exclude
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sticky_note_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private StickyNote stickyNote;

    @Column(name = "link")
    @NotEmpty
    private String link;
}
