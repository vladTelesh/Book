package com.effectivesoft.bookservice.rest.controller;


import com.effectivesoft.bookservice.common.dto.AuthorDto;
import com.effectivesoft.bookservice.common.dto.BookDto;
import com.effectivesoft.bookservice.core.model.Author;
import com.effectivesoft.bookservice.core.model.Book;
import com.effectivesoft.bookservice.core.model.Image;
import com.effectivesoft.bookservice.rest.exception.InternalServerErrorException;
import com.effectivesoft.bookservice.rest.service.AuthorService;
import com.effectivesoft.bookservice.rest.service.RequestParser;
import com.effectivesoft.bookservice.rest.service.Validator;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/authors", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthorController {
    private final RequestParser requestParser;
    private final AuthorService authorService;
    private final Validator validator;
    private final Mapper mapper;

    public AuthorController(@Autowired RequestParser requestParser,
                            @Autowired AuthorService authorService,
                            @Autowired Validator validator,
                            @Autowired Mapper mapper) {
        this.requestParser = requestParser;
        this.authorService = authorService;
        this.validator = validator;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity createAuthor(@Valid @RequestBody AuthorDto authorDto) {
        Optional<Author> author = authorService.createAuthor(mapper.map(authorDto, Author.class));

        return new ResponseEntity<>(mapper.map(author.orElseThrow(InternalServerErrorException::new),
                AuthorDto.class), HttpStatus.CREATED);
    }

    @PostMapping("/{authorId}")
    public ResponseEntity createAuthorImage(@RequestParam(value = "image") MultipartFile image,
                                            @PathVariable String authorId) throws IOException {
        if (!validator.extensionValidator(Objects.requireNonNull(image.getContentType()))) {
            return new ResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        if (authorService.updateAuthorPhoto(image, authorId)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            throw new InternalServerErrorException();
        }
    }

    @GetMapping
    public ResponseEntity readBooks(@RequestParam(value = "name", required = false) String name,
                                    @RequestParam(value = "generated") Boolean generated,
                                    @RequestParam(value = "limit") Integer limit,
                                    @RequestParam(value = "offset") Integer offset,
                                    @RequestParam(value = "sort", required = false) String[] sort) {
        List<Author> authors = authorService.readAuthors(name, generated, limit, offset, requestParser.parseSortParam(sort));
        List<AuthorDto> authorsDto = new ArrayList<>();
        for (Author author : authors) {
            authorsDto.add(mapper.map(author, AuthorDto.class));
        }
        return ResponseEntity.ok(authorsDto);
    }

    @GetMapping("/find")
    public ResponseEntity readAuthorsByName(@RequestParam(value = "name") String name) {
        List<Author> authors = authorService.readAuthorsByName(name);
        List<AuthorDto> authorsDto = new ArrayList<>();

        for (Author author : authors) {
            authorsDto.add(mapper.map(author, AuthorDto.class));
        }

        return ResponseEntity.ok(authorsDto);
    }

    @GetMapping(value = "/count")
    public ResponseEntity readBooksCount(@RequestParam(value = "name", required = false) String name,
                                         @RequestParam(value = "generated") Boolean generated) {
        return ResponseEntity.ok(authorService.readAuthorsCount(name, generated));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity readAuthor(@PathVariable(value = "id") String id) {
        Optional<Author> author = authorService.readAuthor(id);
        return author.map(value -> ResponseEntity.ok(mapper.map(value, AuthorDto.class)))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/{id}/books")
    public ResponseEntity readAuthorsBooks(@PathVariable(value = "id") String id,
                                           @RequestParam(value = "limit") Integer limit,
                                           @RequestParam(value = "offset") Integer offset) {
        List<Book> books = authorService.readAuthorsBooks(id, limit, offset);
        List<BookDto> booksDto = new ArrayList<>();

        for (Book book : books) {
            booksDto.add(mapper.map(book, BookDto.class));
        }

        return ResponseEntity.ok(booksDto);
    }

    @PutMapping
    public ResponseEntity updateAuthor(@RequestBody AuthorDto authorDto) {
        Optional<Author> author = authorService.updateAuthor(mapper.map(authorDto, Author.class));

        return ResponseEntity.ok(mapper.map(author.orElseThrow(InternalServerErrorException::new), AuthorDto.class));
    }
}
