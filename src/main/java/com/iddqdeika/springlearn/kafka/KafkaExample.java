package com.iddqdeika.springlearn.kafka;

import com.iddqdeika.springlearn.SpringlearnApplication;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.*;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@EnableKafka
@EnableConfigurationProperties(TopologyConfig.class)
public class KafkaExample {

    Logger logger = LoggerFactory.getLogger(SpringlearnApplication.class);


    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    private static final Serde<String> STRING_SERDE = Serdes.String();

    @Bean
    @Autowired
    public NewTopic topic(TopologyConfig topologyConfig) {
        return TopicBuilder.name(topologyConfig.getWordsTopic())
                .partitions(10)
                .replicas(1)
                .build();
    }

    @Bean
    @Autowired
    public NewTopic targetTopic(TopologyConfig topologyConfig) {
        return TopicBuilder.name(topologyConfig.getCountTopic())
                .partitions(10)
                .replicas(1)
                .build();
    }


    @Bean
    @Autowired
    public ApplicationRunner runner(KafkaTemplate<String, String> template, TopologyConfig topologyConfig) {
        return args -> {
            while (true) {
                Thread.sleep(1000);
                //logger.info("try to send msg...");
                template.send(topologyConfig.getWordsTopic(), "test MSG");
            }
        };
    }

    @KafkaListener(id = "inputListener", topics = "input")
    public void listenIn(ConsumerRecord consumerRecord) {

        //logger.info("got msg " + consumerRecord.value() + " with offset " + consumerRecord.offset() + " within partition " + consumerRecord.partition());
    }

    @KafkaListener(id = "outputListener", topics = "output")
    public void listenOut(ConsumerRecord consumerRecord) {
        //logger.info("got output msg " + consumerRecord.value());
    }


}
