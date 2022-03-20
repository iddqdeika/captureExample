package com.iddqdeika.springlearn.kafka.streams;

import com.iddqdeika.springlearn.kafka.TopologyConfig;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

public class StringKeyValueMapper implements KeyValueMapper<String, String, String> {

    @Override
    public String apply(String k, String v) {
        return v;
    }
}
