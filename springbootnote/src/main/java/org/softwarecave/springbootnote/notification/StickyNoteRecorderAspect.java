package org.softwarecave.springbootnote.notification;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.notification.kafka.KafkaStickyNoteProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class StickyNoteRecorderAspect {

    private KafkaStickyNoteProducer kafkaStickyNoteProducer;

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


        if (kafkaStickyNoteProducer != null) {
            kafkaStickyNoteProducer.sendToKafka((StickyNote) returnValue);
        } else {
            log.warn("KafkaStickyNoteProducer is not available, skipping Kafka notification");
        }
    }

    @Autowired(required = false)
    public void setKafkaStickyNoteProducer(KafkaStickyNoteProducer kafkaStickyNoteProducer) {
        this.kafkaStickyNoteProducer = kafkaStickyNoteProducer;
    }
}
