package org.softwarecave.springbootnote.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.outbox.service.queue.OutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Aspect
@Slf4j
public class StickyNoteRecorderAspect {

    private List<OutboxService> outboxServices;

    @AfterReturning(pointcut = "@annotation(rec)",
            returning = "returnValue")
    public void addStickyNote(JoinPoint joinPoint, Recordable rec, Object returnValue) {
        if (rec.modelType() == ModelType.STICKY_NOTE) {
            if (rec.actionType() == ActionType.DELETE) {
                log.info("Deleted StickyNote with ID " + joinPoint.getArgs()[0]);
            } else if (rec.actionType() == ActionType.ADD) {
                log.info("Added StickyNote " + returnValue);
            } else if (rec.actionType() == ActionType.UPDATE) {
                log.info("Updated StickyNote " + returnValue);
            }
        }

        sendToOutbox((StickyNote) returnValue);
    }

    private void sendToOutbox(StickyNote stickyNote) {
        if (outboxServices != null && !outboxServices.isEmpty()) {
            outboxServices.forEach(outboxService -> outboxService.send(stickyNote));
        } else {
            log.warn("No outbox services are available, skipping Kafka notification");
        }
    }

    @Autowired(required = false)
    public void setOutboxService(List<OutboxService> outboxServices) {
        this.outboxServices = outboxServices;
    }
}
