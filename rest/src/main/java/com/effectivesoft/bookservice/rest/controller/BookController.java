package com.effectivesoft.bookservice.rest.controller;

import com.effectivesoft.bookservice.core.model.Book;
import com.effectivesoft.bookservice.rest.exception.InternalServerErrorException;
import com.effectivesoft.bookservice.rest.service.BookService;
import com.effectivesoft.bookservice.rest.service.RequestParser;
import com.effectivesoft.bookservice.common.dto.BookDto;
import com.effectivesoft.bookservice.rest.service.Validator;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/books", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController {

    private final RequestParser requestParser;
    private final BookService bookService;
    private final Validator validator;
    private final Mapper mapper;

    BookController(@Autowired RequestParser requestParser,
                   @Autowired BookService bookService,
                   @Autowired Validator validator,
                   @Autowired Mapper mapper) {
        this.requestParser = requestParser;
        this.bookService = bookService;
        this.validator = validator;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity createBook(@Valid @RequestBody BookDto bookDto) {
        Optional<Book> book = bookService.createBook(mapper.map(bookDto, Book.class));

        return new ResponseEntity<>(book.orElseThrow(InternalServerErrorException::new), HttpStatus.CREATED);
    }

    @PostMapping("/images/{bookId}")
    public ResponseEntity createAuthorImage(@RequestParam(value = "image") MultipartFile image,
                                            @PathVariable String bookId) throws IOException {
        if (!validator.extensionValidator(Objects.requireNonNull(image.getContentType()))) {
            return new ResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        if (bookService.updateBookImage(image, bookId)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            throw new InternalServerErrorException();
        }
    }

    @GetMapping
    public ResponseEntity readBooks(@RequestParam(value = "limit") Integer limit,
                                    @RequestParam(value = "offset") Integer offset,
                                    @RequestParam(value = "sort", required = false) String[] sort) {
        List<Book> books = bookService.readBooks(limit, offset, requestParser.parseSortParam(sort));
        List<BookDto> booksDto = new ArrayList<>();
        for (Book book : books) {
            booksDto.add(mapper.map(book, BookDto.class));
        }
        return ResponseEntity.ok(booksDto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity readBook(@PathVariable String id) {
        Optional<Book> book = bookService.readBook(id);
        return book.map(value -> ResponseEntity.ok(mapper.map(value, BookDto.class))).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/count")
    public ResponseEntity readBooksCount(@RequestParam(value = "title", required = false) String title) {
        if (title != null) {
            return ResponseEntity.ok(bookService.readBooksCount(title));
        } else {
            return ResponseEntity.ok(bookService.readBooksCount());
        }
    }

    @GetMapping("/find")
    public ResponseEntity readBooksByTitle(@RequestParam("title") String title) {
        List<Book> books = bookService.readBooksByTitle(title);
        List<BookDto> booksDto = new ArrayList<>();
        for (Book book : books) {
            booksDto.add(mapper.map(book, BookDto.class));
        }
        return ResponseEntity.ok(booksDto);
    }

    @PutMapping
    public ResponseEntity updateBook(@Valid @RequestBody BookDto bookDto) {
        Optional<Book> book = bookService.updateBook(mapper.map(bookDto, Book.class));

        return ResponseEntity.ok(book.orElseThrow(InternalServerErrorException::new));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        return new ResponseEntity<>(200, HttpStatus.OK);
    }
}