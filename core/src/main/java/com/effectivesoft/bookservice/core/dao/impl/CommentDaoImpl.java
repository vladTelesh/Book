package com.effectivesoft.bookservice.core.dao.impl;

import com.effectivesoft.bookservice.core.dao.AbstractDao;
import com.effectivesoft.bookservice.core.dao.CommentDao;
import com.effectivesoft.bookservice.core.model.Comment;
import com.effectivesoft.bookservice.core.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;


@Repository
public class CommentDaoImpl extends AbstractDao<Comment> implements CommentDao {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    protected Class<Comment> getEntityType() {
        return Comment.class;
    }

    @Override
    public List<Comment> readBookComments(String bookId, Integer limit, Integer offset) {
        List<Comment> comments = (List<Comment>) entityManager.createNativeQuery("SELECT * FROM comment WHERE book_id = ?1 ORDER BY date_added DESC LIMIT ?2, ?3", Comment.class)
                .setParameter(1, bookId)
                .setParameter(2, offset)
                .setParameter(3, limit)
                .getResultList();
        return comments;
    }

    @Override
    public long readLikesCount(String commentId) {
        return ((BigInteger) entityManager.createNativeQuery("SELECT COUNT(*) FROM comment_has_like WHERE comment_id = ?1")
                .setParameter(1, commentId).getSingleResult()).longValue();
    }

    @Override
    public List<User> readBookCommentRatedUsers(String commentId, int limit, int offset) {
        return (List<User>) entityManager.createNativeQuery("SELECT * FROM user WHERE id in" +
                "(SELECT user_id FROM comment_has_like WHERE comment_id = ?1 ORDER BY comment_has_like.id DESC)", User.class)
                .setParameter(1, commentId)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public long readBookCommentsCount(String bookId) {
        return ((BigInteger) entityManager.createNativeQuery("SELECT COUNT(*) FROM comment WHERE book_id = ?1")
                .setParameter(1, bookId)
                .getSingleResult()).longValue();
    }


    @Override
    public long isLiked(String commentId, String userId) {
        return ((BigInteger) entityManager.createNativeQuery("SELECT COUNT(*) FROM comment_has_like WHERE comment_id = ?1 AND user_id = ?2")
                .setParameter(1, commentId)
                .setParameter(2, userId)
                .getSingleResult()).longValue();
    }

    @Override
    public int like(String commentId, String userId) {
        return entityManager.createNativeQuery("INSERT INTO comment_has_like (comment_id, user_id) VALUES (?1, ?2)")
                .setParameter(1, commentId)
                .setParameter(2, userId).executeUpdate();
    }

    @Override
    public int dislike(String commentId, String userId) {
        return entityManager.createNativeQuery("DELETE FROM comment_has_like WHERE comment_id = ?1 AND user_id = ?2")
                .setParameter(1, commentId)
                .setParameter(2, userId)
                .executeUpdate();
    }
}
