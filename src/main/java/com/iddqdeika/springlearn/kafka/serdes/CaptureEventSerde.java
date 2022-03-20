package com.iddqdeika.springlearn.kafka.serdes;

import com.iddqdeika.springlearn.domain.CaptureEvent;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class CaptureEventSerde implements Serde<CaptureEvent> {

    Serializer<CaptureEvent> serializer = new JsonSerializer<CaptureEvent>();
    Deserializer<CaptureEvent> deserializer = new JsonDeserializer<>(CaptureEvent.class)
            .ignoreTypeHeaders();

    @Override
    public Serializer<CaptureEvent> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<CaptureEvent> deserializer() {
        return deserializer;
    }
}
