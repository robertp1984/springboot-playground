package org.softwarecave.springbootnote.outbox.service;

import org.softwarecave.springbootnote.outbox.model.Outbox;
import org.softwarecave.springbootnote.outbox.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {
    Page<Outbox> findByStatus(Status status, PageRequest createdDate);
}
