package com.effectivesoft.bookservice.rest.service;

import com.effectivesoft.bookservice.core.dao.CommentDao;
import com.effectivesoft.bookservice.core.model.Comment;
import com.effectivesoft.bookservice.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommentService {
    private static final int RATED_USERS_IN_PREVIEW = 4;

    private final UserProfileService userProfileService;
    private final CommentDao commentDao;

    CommentService(@Autowired CommentDao commentDao, UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
        this.commentDao = commentDao;
    }

    @Transactional
    public Optional<Comment> createBookComment(Comment comment) {
        comment.setId(UUID.randomUUID().toString());
        comment.setUserId(userProfileService.getUserId());
        comment.setDateAdded(LocalDateTime.now());
        comment.getUser().setId(userProfileService.getUserId());
        return commentDao.create(comment);
    }

    public List<Comment> readBookComments(String bookId, Integer limit, Integer offset) {
        return commentDao.readBookComments(bookId, limit, offset);
    }

    public long readLikesCount(String commentId) {
        return commentDao.readLikesCount(commentId);
    }

    public long readBookCommentsCount(String bookId) {
        return commentDao.readBookCommentsCount(bookId);
    }

    public List<User> readBookCommentRatedUsersPreview(String commentId) {
        return commentDao.readBookCommentRatedUsers(commentId, RATED_USERS_IN_PREVIEW, 0);
    }

    public List<User> readBookCommentRatedUsers(String commentId, int limit, int offset) {
        return commentDao.readBookCommentRatedUsers(commentId, limit, offset);
    }

    public Boolean isLiked(String commentId, String userId) {
        return commentDao.isLiked(commentId, userId) != 0;
    }

    @Transactional
    public boolean likeComment(String commentId) {
        return commentDao.like(commentId, userProfileService.getUserId()) == 1;
    }

    @Transactional
    public boolean dislikeComment(String commentId) {
        return commentDao.dislike(commentId, userProfileService.getUserId()) == 1;
    }
}
