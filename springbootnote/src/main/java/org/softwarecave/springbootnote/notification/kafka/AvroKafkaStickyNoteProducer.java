package org.softwarecave.springbootnote.notification.kafka;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.softwarecave.springbootnote.avro.StickyNoteLink;
import org.softwarecave.springbootnote.note.model.StickyNote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Properties;

@Slf4j
@Component
@ConditionalOnBooleanProperty(prefix = "app.kafka.avro", name = "enabled", havingValue = true)
public class AvroKafkaStickyNoteProducer implements KafkaStickyNoteProducer {

    private final String bootstrapServers;
    private final String noteTopic;
    private final String schemaRegistryUrl;

    private KafkaProducer<Long, org.softwarecave.springbootnote.avro.StickyNote> kafkaProducer;

    public AvroKafkaStickyNoteProducer(@Value("${app.kafka.bootstrap-servers}") String bootstrapServers,
                                       @Value("${app.kafka.avro.stickynote-topic}") String noteTopic,
                                       @Value("${app.kafka.schema-registry-url}") String schemaRegistryUrl) {
        this.bootstrapServers = bootstrapServers;
        this.noteTopic = noteTopic;
        this.schemaRegistryUrl = schemaRegistryUrl;
    }

    @PostConstruct
    public void init() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty("schema.registry.url", schemaRegistryUrl);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
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

    @Override
    public void sendToKafka(StickyNote value) {
        var stickyNoteAvro = convertToAvro(value);

        Long key = stickyNoteAvro.getId();
        var producerRecord = new ProducerRecord<>(noteTopic, key, stickyNoteAvro);
        kafkaProducer.send(producerRecord, (RecordMetadata metadata, Exception exception) -> {
            if (exception != null) {
                log.error("Failed to send message to Kafka", exception);
            } else {
                log.info("Message sent to Kafka topic {} partition {} with offset {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }

    private static org.softwarecave.springbootnote.avro.StickyNote convertToAvro(StickyNote value) {
        var linksAvro = value.getLinks().stream().map(link ->
                StickyNoteLink.newBuilder()
                        .setId(link.getId())
                        .setStickyNoteId(link.getStickyNote().getId())
                        .setLink(link.getLink())
                        .build()
        ).toList();

        return org.softwarecave.springbootnote.avro.StickyNote.newBuilder()
                .setId(value.getId())
                .setTitle(value.getTitle())
                .setBody(value.getBody())
                .setType(value.getType().name())
                .setCreated(value.getCreated().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setLinks(linksAvro)
                .build();
    }

}
