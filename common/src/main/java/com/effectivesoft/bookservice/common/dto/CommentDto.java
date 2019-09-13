package com.effectivesoft.bookservice.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

public class CommentDto {
    private String id;
    @NotEmpty
    @JsonProperty("book_id")
    private String bookId;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("user_first_name")
    private String userFirstName;
    @JsonProperty("user_last_name")
    private String userLastName;
    @JsonProperty("user_main_image_link")
    private String userMainImageLink;
    @JsonProperty("date_added")
    private LocalDateTime dateAdded;
    @NotEmpty
    private String text;
    @JsonProperty("likes_count")
    private Integer likesCount;
    @JsonProperty("is_liked")
    private boolean isLiked;
    @JsonProperty("rated_users")
    private List<UserDto> ratedUsers;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserMainImageLink() {
        return userMainImageLink;
    }

    public void setUserMainImageLink(String userMainImageLink) {
        this.userMainImageLink = userMainImageLink;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public List<UserDto> getRatedUsers() {
        return ratedUsers;
    }

    public void setRatedUsers(List<UserDto> ratedUsers) {
        this.ratedUsers = ratedUsers;
    }
}
