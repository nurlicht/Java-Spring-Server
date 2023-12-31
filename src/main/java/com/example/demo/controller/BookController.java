package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.kafka.KafkaPublisher;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/")
public class BookController {

    @Autowired
    private final BookRepository repository;

    @Autowired
    private final KafkaPublisher kafkaPublisher;

    @GetMapping("books")
    public List<Book> get() {
        return repository.findAll();
    }

    @PostMapping("book")
    public Book post(@RequestBody final Book book) {
        kafkaPublisher.send(book);
        final Book persistedBook = repository.save(book);
        kafkaPublisher.send(persistedBook);
        return persistedBook;
    }

}
