package org.softwarecave.springbootnote.notification.kafka;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.Properties;

@Slf4j
@Component
@ConditionalOnBooleanProperty(prefix = "app.kafka.json", name = "enabled", havingValue = true)
public class JsonKafkaStickyNoteProducer implements KafkaStickyNoteProducer {

    private final JsonMapper jsonMapper;
    private final String bootstrapServers;
    private final String noteTopic;

    private KafkaProducer<Long, String> kafkaProducer;
    private final StickyNoteConverter stickyNoteConverter;

    public JsonKafkaStickyNoteProducer(JsonMapper jsonMapper,
                                       @Value("${app.kafka.bootstrap-servers}") String bootstrapServers,
                                       @Value("${app.kafka.json.stickynote-topic}") String noteTopic) {
        this.jsonMapper = jsonMapper;
        this.bootstrapServers = bootstrapServers;
        this.noteTopic = noteTopic;

        this.stickyNoteConverter = new StickyNoteConverter();
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

    @Override
    public void sendToKafka(StickyNote value) {
        StickyNoteDTO dto = stickyNoteConverter.convertToDTO(value);
        Long key = dto.getId();
        String jsonValue = jsonMapper.writeValueAsString(dto);
        var producerRecord = new ProducerRecord<>(noteTopic, key, jsonValue);
        kafkaProducer.send(producerRecord, (RecordMetadata metadata, Exception exception) -> {
            if (exception != null) {
                log.error("Failed to send message to Kafka", exception);
            } else {
                log.info("Message sent to Kafka topic {} partition {} with offset {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }

}
