package org.softwarecave.springbootnote.outbox.kafka;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.softwarecave.springbootnote.note.web.StickyNoteDTO;
import org.softwarecave.springbootnote.note.web.converter.StickyNoteConverter;
import org.softwarecave.springbootnote.outbox.model.Outbox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.Properties;
import java.util.concurrent.Future;

@Slf4j
@Component
@ConditionalOnBooleanProperty(prefix = "app.kafka.json", name = "enabled", havingValue = true)
public class KafkaJsonProducer {

    private final String bootstrapServers;
    private final String noteTopic;

    private KafkaProducer<Long, String> kafkaProducer;

    public KafkaJsonProducer(
                             StickyNoteConverter stickyNoteConverter,
                             @Value("${app.kafka.bootstrap-servers}") String bootstrapServers,
                             @Value("${app.kafka.json.stickynote-topic}") String noteTopic) {
        this.bootstrapServers = bootstrapServers;
        this.noteTopic = noteTopic;
    }

    @PostConstruct
    public void init() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "50");
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");

        log.info("Creating Kafka producer with configuration: {} ", properties);
        kafkaProducer = new KafkaProducer<>(properties);
    }

    @PreDestroy
    public void destroy() {
        log.info("Closing Kafka producer");
        kafkaProducer.close();
        log.info("Closed Kafka producer");
    }

    public Future<RecordMetadata> sendToKafka(Outbox value) {
        var producerRecord = new ProducerRecord<>(noteTopic, value.getAggregateId(), value.getPayloadString());
        return kafkaProducer.send(producerRecord);
    }

}
