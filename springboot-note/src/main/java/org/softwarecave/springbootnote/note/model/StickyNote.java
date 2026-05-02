package org.softwarecave.springbootnote.note.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "STICKY_NOTE")
public class StickyNote {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STICKY_NOTE_SEQ")
    @SequenceGenerator(name = "STICKY_NOTE_SEQ", sequenceName = "STICKY_NOTE_SEQ", allocationSize = 1)
    @Column(name = "id")
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(name = "title")
    @NotBlank
    private String title;

    @Column(name = "body")
    @NotBlank
    private String body;

    @Column(name = "type")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @OneToMany(mappedBy = "stickyNote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    private List<StickyNoteLink> links = new ArrayList<>();

    @OneToMany(mappedBy = "stickyNote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    private List<StickyNoteTag> tags = new ArrayList<>();

    @Column(name = "created")
    @PastOrPresent
    @NotNull
    private LocalDateTime created;
}
