package com.effectivesoft.bookservice.ui.client;

import com.effectivesoft.bookservice.common.dto.AnnualUserStatsDto;
import com.effectivesoft.bookservice.common.dto.MonthlyUserStatsDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.effectivesoft.bookservice.common.dto.UserBookDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class UserBookRestClient extends RestClient {
    private final ObjectMapper objectMapper;

    UserBookRestClient(@Autowired ObjectMapper objectMapper) {
        super(objectMapper);
        this.objectMapper = objectMapper;
    }

    public boolean createUserBook(UserBookDto userBookDto) throws JsonProcessingException {
        Optional<Response> response = postRequest("/user_books", userBookDto);
        return response.filter(value -> value.getStatusCode() == 201).isPresent();
    }

    public List<UserBookDto> readUserBooks(int limit, int offset, List<String> sort) throws IOException {
        StringBuilder queryString = new StringBuilder("/user_books?limit=" + limit + "&offset=" + offset);
        if (!sort.isEmpty()) {
            for (String s : sort)
                queryString.append("&sort=").append(s);
        }

        Optional<Response> response = getRequest(queryString.toString());

        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), new TypeReference<List<UserBookDto>>() {
                });
            } else {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }

    public Integer readUserBooksCount() throws IOException {
        Optional<Response> response = getRequest("/user_books/count");
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), Integer.class);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public List<AnnualUserStatsDto> readUserStats(int from, int to) throws IOException {
        Optional<Response> response = getRequest("/user_books/stats?from=" + from + "&to=" + to);
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), new TypeReference<List<AnnualUserStatsDto>>() {
                });
            }
        }

        return Collections.emptyList();
    }

    public List<MonthlyUserStatsDto> readUserStats(int year) throws IOException {
        Optional<Response> response = getRequest("/user_books/stats?year=" + year);

        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), new TypeReference<List<MonthlyUserStatsDto>>() {
                });
            }
        }

        return Collections.emptyList();
    }

    public boolean updateUserBook(UserBookDto userBookDto) throws JsonProcessingException {
        Optional<Response> response = putRequest("/user_books", userBookDto);
        return response.filter(value -> value.getStatusCode() == 200).isPresent();
    }

    public boolean deleteUserBook(String id) {
        Optional<Response> response = deleteRequest("/user_books/" + id);
        return response.filter(value -> value.getStatusCode() == 200).isPresent();
    }
}
