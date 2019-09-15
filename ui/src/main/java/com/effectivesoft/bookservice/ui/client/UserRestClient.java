package com.effectivesoft.bookservice.ui.client;

import com.effectivesoft.bookservice.common.dto.GoogleUserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.effectivesoft.bookservice.common.dto.ImageDto;
import com.effectivesoft.bookservice.common.dto.PasswordsDto;
import com.effectivesoft.bookservice.common.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class UserRestClient extends RestClient {
    private static final String DEFAULT_USER_MAIN_IMAGE_LINK = "https://i.ibb.co/JH6g0T7/no-image.png";
    private final ObjectMapper objectMapper;

    public UserRestClient(@Autowired ObjectMapper objectMapper) {
        super(objectMapper);
        this.objectMapper = objectMapper;
    }

    public boolean createUser(UserDto user) throws IOException {
        Optional<Response> response = postRequest("/users", user);
        return response.filter(value -> value.getStatusCode() == 200).isPresent();
    }

    public boolean createGoogleUser(GoogleUserDto googleUser) throws JsonProcessingException {
        Optional<Response> response = postRequest("/users/google", googleUser);
        return response.filter(value -> value.getStatusCode() == 200).isPresent();
    }

    public boolean createUserImage(String filename, String fileContentType, byte[] byteArray) {
        Optional<Response> response = postRequest("/users/images", filename, fileContentType, byteArray);
        return response.filter(value -> value.getStatusCode() == 200).isPresent();
    }

    public UserDto readUser() throws IOException {
        Optional<Response> response = getRequest("/users");
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), UserDto.class);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Optional<UserDto> readUser(String username, boolean google) throws IOException {
        Optional<Response> response = postRequest("/users/login?google=" + google, username);
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return Optional.ofNullable(objectMapper.readValue(response.get().getBody(), UserDto.class));
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public String readUserMainImage() throws IOException {
        Optional<Response> response = getRequest("/users/images/main");
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return (objectMapper.readValue(response.get().getBody(), ImageDto.class)).getLink();
            }
        }
        return DEFAULT_USER_MAIN_IMAGE_LINK;
    }

    public List<ImageDto> readUserImages() throws IOException {
        Optional<Response> response = getRequest("/users/images");
        if (response.isPresent()) {
            if (response.get().getStatusCode() == 200) {
                return objectMapper.readValue(response.get().getBody(), new TypeReference<List<ImageDto>>() {
                });
            }
        }
        return Collections.emptyList();
    }

    public boolean updateUserMainImage(ImageDto imageDto) throws JsonProcessingException {
        Optional<Response> response = putRequest("/users/images", imageDto);
        return response.filter(value -> value.getStatusCode() == 200).isPresent();
    }

    public boolean updateUser(UserDto userDto) throws JsonProcessingException {
        Optional<Response> response = putRequest("/users", userDto);
        return response.filter(value -> value.getStatusCode() == 200).isPresent();
    }

    public boolean updateUserPassword(PasswordsDto passwords) throws JsonProcessingException {
        Optional<Response> response = putRequest("/users/password", passwords);
        return response.filter(value -> value.getStatusCode() == 200).isPresent();
    }

    public boolean confirmUser(String code) {
        Optional<Response> response = getRequest("/users" + "/confirm/" + code);
        return response.filter(value -> value.getStatusCode() == 200).isPresent();
    }
}