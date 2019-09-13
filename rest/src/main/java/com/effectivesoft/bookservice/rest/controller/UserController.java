package com.effectivesoft.bookservice.rest.controller;


import com.effectivesoft.bookservice.core.model.User;
import com.effectivesoft.bookservice.rest.exception.InternalServerErrorException;
import com.effectivesoft.bookservice.rest.service.UserProfileService;
import com.effectivesoft.bookservice.rest.service.UserService;
import com.effectivesoft.bookservice.common.dto.PasswordsDto;
import com.effectivesoft.bookservice.common.dto.UserDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(value = "api/v1/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserProfileService userProfileService;
    private final Mapper mapper;
    private final UserService userService;

    UserController(@Autowired UserProfileService userProfileService,
                   @Autowired Mapper mapper,
                   @Autowired UserService userService) {
        this.userProfileService = userProfileService;
        this.mapper = mapper;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity createUser(@Valid @RequestBody UserDto userDto) {
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Optional<User> user = userService.createUser(mapper.map(userDto, User.class));

        return ResponseEntity.ok(mapper.map(user.orElseThrow(InternalServerErrorException::new), UserDto.class));
    }

    @GetMapping
    public ResponseEntity readUser() {
        Optional<User> user = userService.readUser(userProfileService.getUserId());
        return user.map(value -> ResponseEntity.ok(mapper.map(value, UserDto.class)))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping
    public ResponseEntity updateUser(@RequestBody UserDto userDto) {
        Optional<User> user = userService.updateUser(mapper.map(userDto, User.class), userProfileService.getUserId());

        return ResponseEntity.ok(mapper.map(user.orElseThrow(InternalServerErrorException::new), UserDto.class));
    }

    @PutMapping(value = "/password")
    public ResponseEntity updateUserPassword(@Valid @RequestBody PasswordsDto passwords) {
        if (userService.updateUserPassword(userProfileService.getUserId(),
                userProfileService.getUsername(), passwords)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            throw new InternalServerErrorException();
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity readUserByUsername(@RequestBody String username) {
        Optional<User> user = userService.readUserByUsername(username);
        return user.<ResponseEntity>map(value -> ResponseEntity.ok(mapper.map(value, UserDto.class))).orElseGet(() ->
                new ResponseEntity<>(404, HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Integer> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(200, HttpStatus.OK);
    }

    @GetMapping(value = "/confirm/{code}")
    public ResponseEntity confirmUser(@PathVariable String code) {
        boolean isConfirm = userService.confirmUser(code);
        if (isConfirm) {
            return ResponseEntity.ok(true);
        } else {
            throw new InternalServerErrorException();
        }
    }
}
