package com.effectivesoft.bookservice.ui.client;

import com.effectivesoft.bookservice.common.dto.AuthorDto;
import com.effectivesoft.bookservice.common.dto.BookDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class AuthorRestClient extends RestClient {

    private final ObjectMapper objectMapper;

    public AuthorRestClient(ObjectMapper objectMapper) {
        super(objectMapper);
        this.objectMapper = objectMapper;
    }

    public Optional<AuthorDto> createAuthor(AuthorDto author) throws IOException {
        Optional<Response> response = postRequest("/authors", author);

        if (response.isPresent()) {
            if (response.get().getStatusCode() == 201) {
                return Optional.of(objectMapper.readValue(response.get().getBody(), AuthorDto.class));
            }
        }

        return Optional.empty();
    }

    public Optional<AuthorDto> readAuthor(String id) throws IOException {
        Optional<Response> response = getRequest("/authors/" + id);
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return Optional.ofNullable(objectMapper.readValue(response.get().getBody(), AuthorDto.class));
            }
        }

        return Optional.empty();
    }

    public List<AuthorDto> readAuthors(String name, boolean generated, int limit, int offset, List<String> sort) throws IOException {
        StringBuilder queryString = new StringBuilder("/authors?limit=" + limit + "&offset=" + offset);
        if (!sort.isEmpty()) {
            for (String s : sort)
                queryString.append("&sort=").append(s);
        }
        if (name.length() != 0) {
            queryString.append("&name=").append(name);
        }

        if(generated){
            queryString.append("&generated=true");
        } else {
            queryString.append("&generated=false");
        }

        Optional<Response> response = getRequest(queryString.toString());

        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), new TypeReference<List<AuthorDto>>() {
                });
            }
        }
        return Collections.emptyList();
    }

    public List<AuthorDto> readAuthorsByName(String name) throws IOException {
        Optional<Response> response = getRequest("/authors/find?name=" + URLEncoder.encode(name, Charset.forName("UTF-8")));
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), new TypeReference<List<AuthorDto>>() {
                });
            }
        }
        return Collections.emptyList();
    }

    public Integer readAuthorsCount(String name, boolean generated) throws IOException {
        StringBuilder queryString = new StringBuilder("/authors/count?name=" + URLEncoder.encode(name, Charset.forName("UTF-8")));
        if(generated){
            queryString.append("&generated=true");
        } else {
            queryString.append("&generated=false");
        }

        Optional<Response> response = getRequest(queryString.toString());
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), Integer.class);
            }
        }
        return 0;
    }

    public List<BookDto> readAuthorsBooks(String id, int limit, int offset) throws IOException {
        Optional<Response> response = getRequest("/authors/" + id + "/books?limit=" + limit + "&offset=" + offset);
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), new TypeReference<List<BookDto>>() {
                });
            }
        }
        return Collections.emptyList();
    }

    public boolean updateAuthorPhoto(String authorId, String filename, String fileContentType, byte[] byteArray) {
        Optional<Response> response = postRequest("/authors/" + authorId, filename, fileContentType, byteArray);

        return response.filter(value -> value.getStatusCode() == 200).isPresent();

    }

    public Optional<AuthorDto> updateAuthor(AuthorDto author) throws IOException {
        Optional<Response> response = putRequest("/authors", author);

        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return Optional.of(objectMapper.readValue(response.get().getBody(), AuthorDto.class));
            }
        }

        return Optional.empty();
    }
}
