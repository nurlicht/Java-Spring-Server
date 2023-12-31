package com.example.demo.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class KafkaSubscriber {

    @KafkaListener(topics = {KafkaConstants.TOPIC_NAME}, groupId = KafkaConstants.GROUP_ID)
    public void save(final String value) {
        log.info("Received value={}.", value);
    }

}