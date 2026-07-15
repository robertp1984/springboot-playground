package org.softwarecave.springbootnote.outbox.kafka;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.softwarecave.springbootnote.note.web.converter.StickyNoteConverter;
import org.softwarecave.springbootnote.outbox.model.Outbox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.Future;

import static org.softwarecave.springbootnote.outbox.tools.AvroTools.fromBytes;

@Slf4j
@Component
@ConditionalOnBooleanProperty(prefix = "app.kafka.avro", name = "enabled", havingValue = true)
public class KafkaAvroProducer {

    private final String bootstrapServers;
    private final String noteTopic;
    private final String schemaRegistryUrl;

    private KafkaProducer<Long, SpecificRecord> kafkaProducer;

    public KafkaAvroProducer(
            StickyNoteConverter stickyNoteConverter,
            @Value("${app.kafka.bootstrap-servers}") String bootstrapServers,
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
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", schemaRegistryUrl);
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

    public <T extends SpecificRecord> Future<RecordMetadata> sendToKafka(Outbox value, Class<T> avroClass) {
        try {
            var avroObject = fromBytes(value.getPayloadBytes(), avroClass);
            var producerRecord = new ProducerRecord<Long, SpecificRecord>(noteTopic, value.getAggregateId(), avroObject);
            return kafkaProducer.send(producerRecord);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
