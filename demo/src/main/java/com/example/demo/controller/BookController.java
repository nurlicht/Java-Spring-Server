package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
@Log4j2
public class BookController {

    @Autowired
    private final BookRepository repository;

    @GetMapping("books")
    public List<Book> get() {
        return repository.findAll();
    }

    @PostMapping("book")
    public Book post(@RequestBody final Book book) {
        log.info("Payload={}.", book);
        return repository.save(book);
    }

}
