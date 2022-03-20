package com.iddqdeika.springlearn.kafka.streams;

import com.iddqdeika.springlearn.kafka.TopologyConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class WordCountProcessor {

    private static final Serde<String> STRING_SERDE = Serdes.String();
    private static final  Serde<Long> LONG_SERDE = Serdes.Long();
    private volatile KTable<String, Long> wordCounts;

    //@Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        KStream<String, String> messageStream = streamsBuilder
                .stream("input", Consumed.with(STRING_SERDE, STRING_SERDE));

        messageStream
                .mapValues((ValueMapper<String, String>) String::toLowerCase)
                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
                .groupBy(new StringKeyValueMapper(), Grouped.with(STRING_SERDE, STRING_SERDE))
                .count()
                .toStream()
                .to("output");


        KStream<String, Long> outputStream = streamsBuilder
                .stream("output", Consumed.with(STRING_SERDE, LONG_SERDE))
                .peek((s, s2) -> System.out.println( "output: " + s + ":" + s2));
    }
}