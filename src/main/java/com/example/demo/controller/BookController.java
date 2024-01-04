package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.kafka.KafkaPublisher;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
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


    @QueryMapping
    public List<Book> getBooksByLanguage(@Argument @NotNull final String language) {
        return this.get().stream()
            .filter(book -> language.equalsIgnoreCase(book.getLanguage()))
            .collect(Collectors.toUnmodifiableList());
    }

    @MutationMapping
    public Book createBook(
        @Argument final String name,
        @Argument final String publisher,
        @Argument final String isbn,
        @Argument final String language,
        @Argument final List<String> authors
    ) {
        final Book payload = Book.builder()
            .name(name)
            .publisher(publisher)
            .isbn(isbn)
            .language(language)
            .authors(authors)
            .build();
        return repository.save(payload);
    }

}
