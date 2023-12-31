package com.example.demo.kafka;

import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.model.Book;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class KafkaPublisher {

    @Autowired
    private final KafkaTemplate<String, Book> kafkaTemplate;

    @Autowired
    private final NewTopic newTopic;

    public void send(Book value) {
        log.info("Sending value={} ...", value);
        kafkaTemplate.send(newTopic.name(), value);
        log.info("Sending value={} was completed.", value);
    }
}