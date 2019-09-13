package com.effectivesoft.bookservice.ui.client;

import com.effectivesoft.bookservice.common.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.effectivesoft.bookservice.common.dto.CommentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class CommentRestClient extends RestClient {
    private final ObjectMapper objectMapper;

    CommentRestClient(@Autowired ObjectMapper objectMapper) {
        super(objectMapper);
        this.objectMapper = objectMapper;
    }

    public boolean createBookComment(CommentDto commentDto) throws JsonProcessingException {
        Optional<Response> response = postRequest("/comments", commentDto);

        return response.filter(value -> value.getStatusCode() == 201).isPresent();
    }

    public List<CommentDto> readBookComments(String bookId, int limit, int offset) throws IOException {
        Optional<Response> response = getRequest("/comments/" + bookId + "?limit=" + limit + "&offset=" + offset);

        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), new TypeReference<List<CommentDto>>() {
                });
            }
        }
        return Collections.emptyList();
    }

    public Integer readBookCommentsCount(String bookId) throws IOException {
        Optional<Response> response = getRequest("/comments/" + bookId + "/count");
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), Integer.class);
            }
        }
        return 0;
    }

    public List<UserDto> readBookCommentRatedUsers(String commentId, int limit, int offset) throws IOException {
        Optional<Response> response = getRequest("/comments/" + commentId + "/likes?limit=" + limit + "&offset=" + offset);
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), new TypeReference<List<UserDto>>() {
                });
            }
        }
        return Collections.emptyList();
    }

    public boolean likeBookComment(String commentId) throws JsonProcessingException {
        Optional<Response> response = putRequest("/comments/" + commentId + "/like", null);

        return response.filter(value -> value.getStatusCode() == 200).isPresent();
    }

    public boolean dislikeBookComment(String commentId) throws JsonProcessingException {
        Optional<Response> response = putRequest("/comments/" + commentId + "/dislike", null);

        return response.filter(value -> value.getStatusCode() == 200).isPresent();
    }
}
