package org.softwarecave.springbootnote.outbox.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Entity
@Table(name = "outbox")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "outbox_seq", allocationSize = 1)
    private Long id;

    @Column(name = "aggregate_type")
    @Enumerated(EnumType.STRING)
    private AggregateType aggregateType;

    @Column(name = "aggregate_id")
    private Long aggregateId;

    @Column(name = "message_type")
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "payload_bytes")
    private byte[] payloadBytes;

    @Column(name = "payload_string")
    private String payloadString;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
}
