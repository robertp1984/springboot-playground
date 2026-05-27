package org.softwarecave.springbootnotecategorizer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.softwarecave.springbootnotecategorizer.categorizer.Categorizer;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;

import java.util.HashMap;

@Slf4j
@Configuration
@EnableKafkaStreams
public class StickyNoteKafkaStreamsConfig {

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kafkaStreamsConfig(@Value("${app.kafka.bootstrap-servers}") String bootstrapServers) {
        var props = new HashMap<String, Object>();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stickynote-categorizer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer configurer() {
        return fb -> fb.setStateListener((newState, oldState) -> {
            log.info("State transition from " + oldState + " to " + newState);
        });
    }

    @Bean
    public KStream<Long, String> kafkaStream(@Value("${app.kafka.input-topic}") String inputTopic,
                                             @Value("${app.kafka.output-topic}") String outputTopic,
                                             CategorizerFactory categorizerFactory,
                                             StreamsBuilder builder) {
        Categorizer keywordBasedCategorizer = categorizerFactory.getKeywordBasedCategorizer();

        KStream<Long, String> stickyNoteStream = builder.stream(inputTopic);

        KStream<Long, String> categorizedStickyNoteStream = stickyNoteStream
                .mapValues((value) -> new StickyNoteModifier(value, keywordBasedCategorizer)
                        .addCategories(2)
                        .getModifiedObjectJson());

        categorizedStickyNoteStream.to(outputTopic, Produced.with(Serdes.Long(), Serdes.String()));

        return categorizedStickyNoteStream;
    }
}
