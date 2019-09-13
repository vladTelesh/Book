package com.effectivesoft.bookservice.rest.controller;

import com.effectivesoft.bookservice.common.dto.UserDto;
import com.effectivesoft.bookservice.core.model.Comment;
import com.effectivesoft.bookservice.core.model.User;
import com.effectivesoft.bookservice.rest.exception.InternalServerErrorException;
import com.effectivesoft.bookservice.rest.service.CommentService;
import com.effectivesoft.bookservice.rest.service.UserProfileService;
import com.effectivesoft.bookservice.common.dto.CommentDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/comments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentController {

    private final UserProfileService userProfileService;
    private final CommentService commentService;
    private final Mapper mapper;

    CommentController(@Autowired UserProfileService userProfileService,
                      @Autowired CommentService commentService,
                      @Autowired Mapper mapper) {
        this.userProfileService = userProfileService;
        this.commentService = commentService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity createBookComment(@RequestBody CommentDto commentDto) {
        if (commentService.createBookComment(mapper.map(commentDto, Comment.class)).isPresent()) {
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            throw new InternalServerErrorException();
        }
    }


    @GetMapping(value = "/{bookId}")
    public ResponseEntity readBookComments(@PathVariable(value = "bookId") String bookId,
                                           @RequestParam(value = "limit") Integer limit,
                                           @RequestParam(value = "offset") Integer offset) {
        List<Comment> comments = commentService.readBookComments(bookId, limit, offset);
        List<CommentDto> commentsDto = new ArrayList<>();

        for (Comment comment : comments) {
            List<User> ratedUsers = commentService.readBookCommentRatedUsersPreview(comment.getId());
            CommentDto commentDto = mapper.map(comment, CommentDto.class);
            commentsDto.add(commentDto);
            commentDto.setRatedUsers(new ArrayList<>());
            for (User user : ratedUsers) {
                commentDto.getRatedUsers().add(mapper.map(user, UserDto.class));
            }
        }

        for (CommentDto commentDto : commentsDto) {
            commentDto.setLikesCount((int) commentService.readLikesCount(commentDto.getId()));
            commentDto.setLiked(commentService.isLiked(commentDto.getId(), userProfileService.getUserId()));
        }

        return ResponseEntity.ok(commentsDto);
    }

    @GetMapping(value = "/{bookId}/count")
    public ResponseEntity readBookCommentsCount(@PathVariable(value = "bookId") String bookId) {
        return ResponseEntity.ok(commentService.readBookCommentsCount(bookId));
    }

    @GetMapping(value = "/{commentId}/likes")
    public ResponseEntity readBookCommentRatedUsers(@PathVariable(value = "commentId") String commentId,
                                                    @RequestParam(value = "limit") Integer limit,
                                                    @RequestParam(value = "offset") Integer offset) {
        List<User> users = commentService.readBookCommentRatedUsers(commentId, limit, offset);
        List<UserDto> usersDto = new ArrayList<>();

        for (User user : users) {
            usersDto.add(mapper.map(user, UserDto.class));
        }
        return ResponseEntity.ok(usersDto);
    }

    @PutMapping(value = "/{commentId}/like")
    public ResponseEntity likeBookComment(@PathVariable(value = "commentId") String commentId) {
        if (commentService.likeComment(commentId)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            throw new InternalServerErrorException();
        }
    }

    @PutMapping(value = "/{commentId}/dislike")
    public ResponseEntity dislikeBookComment(@PathVariable(value = "commentId") String commentId) {
        if (commentService.dislikeComment(commentId)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            throw new InternalServerErrorException();
        }
    }

}
