package com.iddqdeika.springlearn.kafka.serdes;

import com.iddqdeika.springlearn.domain.ItemWithStructureMaps;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class ItemWithStructureMapsSerde implements Serde<ItemWithStructureMaps> {

    Serializer<ItemWithStructureMaps> serializer = new JsonSerializer<>();
    Deserializer<ItemWithStructureMaps> deserializer = new JsonDeserializer<>(ItemWithStructureMaps.class)
            .ignoreTypeHeaders();

    @Override
    public Serializer<ItemWithStructureMaps> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<ItemWithStructureMaps> deserializer() {
        return deserializer;
    }
}
