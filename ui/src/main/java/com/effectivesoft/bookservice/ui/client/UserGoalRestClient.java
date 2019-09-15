package com.effectivesoft.bookservice.ui.client;

import com.effectivesoft.bookservice.common.dto.UserGoalDto;
import com.effectivesoft.bookservice.common.dto.UserGoalProgressDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class UserGoalRestClient extends RestClient {
    private final ObjectMapper objectMapper;

    public UserGoalRestClient(@Autowired ObjectMapper objectMapper) {
        super(objectMapper);
        this.objectMapper = objectMapper;
    }

    public Optional<UserGoalDto> createUserGoal(UserGoalDto userGoalDto) throws IOException {
        Optional<Response> response = postRequest("/goal", userGoalDto);

        if (response.isPresent()) {
            if (response.get().getStatusCode() == 201) {
                return Optional.of(objectMapper.readValue(response.get().getBody(), UserGoalDto.class));
            }
        }

        return Optional.empty();
    }

    public Optional<UserGoalDto> readUserGoal() throws IOException {
        Optional<Response> response = getRequest("/goal");

        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return Optional.of(objectMapper.readValue(response.get().getBody(), UserGoalDto.class));
            }
        }
        return Optional.empty();
    }

    public Optional<UserGoalProgressDto> readUserGoalProgress() throws IOException {
        Optional<Response> response = getRequest("/goal/progress");

        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return Optional.of(objectMapper.readValue(response.get().getBody(), UserGoalProgressDto.class));
            }
        }

        return Optional.empty();
    }

    public Optional<UserGoalDto> updateUserGoal(UserGoalDto userGoalDto) throws IOException {
        Optional<Response> response = putRequest("/goal", userGoalDto);

        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return Optional.of(objectMapper.readValue(response.get().getBody(), UserGoalDto.class));
            }
        }
        return Optional.empty();
    }
}
