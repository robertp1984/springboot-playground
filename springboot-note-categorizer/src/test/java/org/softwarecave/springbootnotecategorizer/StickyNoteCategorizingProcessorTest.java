package org.softwarecave.springbootnotecategorizer;

import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.TestRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class StickyNoteCategorizingProcessorTest {

    private static final String INPUT_TOPIC = "input-topic";
    private static final String OUTPUT_TOPIC = "output-topic";

    private TopologyTestDriver topologyTestDriver;

    @BeforeEach
    public void setup() {
        StickyNoteCategorizingProcessor processor = new StickyNoteCategorizingProcessor("localhost:29092", INPUT_TOPIC, OUTPUT_TOPIC,
                new CategorizerFactory());
        topologyTestDriver = new TopologyTestDriver(processor.createTopology(), processor.createConfig());
    }

    @AfterEach
    public void tearDown() {
        topologyTestDriver.close();
    }

    @Test
    public void testForTwoRecords() {
        var inputTopic = topologyTestDriver.createInputTopic(INPUT_TOPIC, new LongSerializer(), new StringSerializer());
        var outputTopic = topologyTestDriver.createOutputTopic(OUTPUT_TOPIC, new LongDeserializer(), new StringDeserializer());

        inputTopic.pipeInput(1L, createSampleCloudNote());
        inputTopic.pipeInput(2L, createSampleKafkaNote());

        TestRecord<Long, String> record1 = outputTopic.readRecord();
        assertThat(record1.key()).isEqualTo(1L);
        assertThat(record1.value()).contains("\"categories\":[\"Cloud\"");

        TestRecord<Long, String> record2 = outputTopic.readRecord();
        assertThat(record2.key()).isEqualTo(2L);
        assertThat(record2.value()).contains("\"categories\":[\"Kafka\"");

        assertThat(outputTopic.isEmpty()).isTrue();
    }

    private String createSampleCloudNote() {
        return """
                {
                    "title": "Cloud Note",
                    "body": "This is a note about cloud computing.",
                    "otherField": "This field should remain unchanged."
                }
                """;
    }

    private String createSampleKafkaNote() {
        return """
                {
                    "title": "Kafka Note",
                    "body": "This is a note about kafka streams.",
                    "otherField": "This field should remain unchanged."
                }
                """;
    }
}
