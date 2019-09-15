package com.effectivesoft.bookservice.ui.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.effectivesoft.bookservice.common.dto.BookDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class BookRestClient extends RestClient {

    private final ObjectMapper objectMapper;

    BookRestClient(@Autowired ObjectMapper objectMapper){
        super(objectMapper);
        this.objectMapper = objectMapper;
    }

    public Optional<BookDto> createBook(BookDto book) throws IOException {
        Optional<Response> response = postRequest("/books", book);

        if(response.isPresent()){
            if(response.get().getStatusCode() == 201){
                return Optional.of(objectMapper.readValue(response.get().getBody(), BookDto.class));
            }
        }

        return Optional.empty();
    }

    public BookDto readBook(String bookId) throws IOException {
        Optional<Response> response = getRequest("/books/" + bookId);
        if(response.isPresent()){
            if(response.get().getStatusCode() == 200){
                return objectMapper.readValue(response.get().getBody(), BookDto.class);
            }
        }
        return null;
    }

    public List<BookDto> readBooks(int limit, int offset, List<String> sort) throws IOException {
        StringBuilder queryString = new StringBuilder("/books?limit=" + limit + "&offset=" + offset);
        if (!sort.isEmpty()) {
            for (String s : sort)
                queryString.append("&sort=").append(s);
        }

        Optional<Response> response = getRequest(queryString.toString());

        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), new TypeReference<List<BookDto>>() {
                });
            }
        }
        return Collections.emptyList();
    }

    public Integer readBooksCount() throws IOException {
        Optional<Response> response = getRequest("/books/count");
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), Integer.class);
            }
        }
        return 0;
    }

    public Integer readBooksCount(String title) throws IOException {
        Optional<Response> response = getRequest("/books/count?title=" + URLEncoder.encode(title, Charset.forName("UTF-8")));
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), Integer.class);
            }
        }
        return 0;
    }

    public List<BookDto> readBooksByTitle(String title) throws IOException {
        Optional<Response> response = getRequest("/books/find?title=" + URLEncoder.encode(title, Charset.forName("UTF-8")));
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), new TypeReference<List<BookDto>>() {
                });
            }
        }
        return Collections.emptyList();
    }

    public Optional<BookDto> updateBook(BookDto book) throws IOException {
        Optional<Response> response = putRequest("/books", book);

        if(response.isPresent()){
            if(response.get().getStatusCode() == 200){
                return Optional.of(objectMapper.readValue(response.get().getBody(), BookDto.class));
            }
        }

        return Optional.empty();
    }

    public boolean updateBookImage(String bookId, String filename, String fileContentType, byte[] byteArray) {
        Optional<Response> response = postRequest("/books/images/" + bookId, filename, fileContentType, byteArray);

        return response.filter(value -> value.getStatusCode() == 200).isPresent();

    }
}