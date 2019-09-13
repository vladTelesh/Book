package com.effectivesoft.bookservice.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class UserDto {
    private String id;
    @NotEmpty
    @Size(min = 2, max = 20)
    @JsonProperty("first_name")
    private String firstName;
    @NotEmpty
    @Size(min = 2, max = 15)
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;
    @JsonProperty("photo_link")
    private String photoLink;
    @NotEmpty
    @Size(min = 2)
    private String username;
    @NotEmpty
    @Size(min = 6)
    private String password;
    @NotEmpty
    @JsonProperty("confirm_password")
    private String confirmPassword;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }
}
