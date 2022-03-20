package com.iddqdeika.springlearn.kafka.streams;

import com.iddqdeika.springlearn.domain.ItemWithStructureMaps;
import com.iddqdeika.springlearn.kafka.serdes.ItemWithStructureMapsSerde;
import com.iddqdeika.springlearn.domain.CaptureEvent;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.*;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.List;
import java.util.Properties;


public class TestNautilusProcessor {

    @Test
    public void givenInputMessages_whenProcessed_thenWordCountIsProduced(){
        // собираем топологию
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        new NautilusEventsProcessor().buildNautilusPipeline(streamsBuilder);
        Topology topology = streamsBuilder.build();

        try (TopologyTestDriver topologyTestDriver = new TopologyTestDriver(topology, new Properties())) {
            // соберем топики
            TestInputTopic<String, CaptureEvent> inputTopic = topologyTestDriver
                    .createInputTopic(NautilusEventsProcessor.TOPIC, new StringSerializer(), new JsonSerializer<>());
            TestOutputTopic<String, ItemWithStructureMaps> outputTopic = topologyTestDriver
                    .createOutputTopic("output", new StringDeserializer(), new ItemWithStructureMapsSerde().deserializer());

            // закинем данные во входящий
            inputTopic.pipeInput("key", new CaptureEvent(NautilusEventsProcessor.STRUCTURE_MAPS_TABLE, 2, "ArticleID", "1"));
            inputTopic.pipeInput("key2", new CaptureEvent(NautilusEventsProcessor.REVISION_TABLE, 2, "ID", "1"));

            // зачитаем исходящий топик
            List list = outputTopic.readKeyValuesToList();

            // проверяем
            Assert.assertEquals(1, list.size());
        }
    }
}
