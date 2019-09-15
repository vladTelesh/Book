package com.effectivesoft.bookservice.core.dao;

import com.effectivesoft.bookservice.core.model.Comment;
import com.effectivesoft.bookservice.core.model.User;

import java.util.List;

public interface CommentDao extends BaseDao<Comment> {
    List<Comment> readBookComments(String bookId, Integer limit, Integer offset);

    long readBookCommentsCount(String bookId);

    long readLikesCount(String commentId);

    List<User> readBookCommentRatedUsers(String commentId, int limit, int offset);

    long isLiked(String commentId, String userId);

    int like(String commentId, String userId);

    int dislike(String commentId, String userId);
}
