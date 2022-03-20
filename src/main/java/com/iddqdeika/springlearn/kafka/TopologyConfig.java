package com.iddqdeika.springlearn.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "kafka.topology")
public class TopologyConfig {

    private String wordsTopic;

    private String countTopic;

    public String getCountTopic() {
        return countTopic;
    }

    public void setCountTopic(String countTopic) {
        this.countTopic = countTopic;
    }

    public String getWordsTopic() {
        return wordsTopic;
    }

    public void setWordsTopic(String wordsTopic) {
        this.wordsTopic = wordsTopic;
    }
}
