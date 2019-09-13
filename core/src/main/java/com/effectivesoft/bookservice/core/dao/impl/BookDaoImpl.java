package com.effectivesoft.bookservice.core.dao.impl;

import com.effectivesoft.bookservice.core.dao.AbstractDao;
import com.effectivesoft.bookservice.core.dao.BookDao;
import com.effectivesoft.bookservice.core.model.Book;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;

@Repository
public class BookDaoImpl extends AbstractDao<Book> implements BookDao {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    protected Class<Book> getEntityType() {
        return Book.class;
    }

    @Override
    public List<Book> readBooks(int limit, int offset, String sort) {
        StringBuilder query = new StringBuilder("SELECT * FROM book");
        if (!sort.equals("")) {
            query.append(" ORDER BY ").append(sort);
        }
        return entityManager.createNativeQuery(query.toString(), Book.class)
                .setMaxResults(limit)
                .setFirstResult(offset)
                .getResultList();
    }

    @Override
    public long readCount() {
        return ((BigInteger) entityManager.createNativeQuery("SELECT COUNT(*) FROM book").getSingleResult()).longValue();
    }

    @Override
    public List<Book> readBooksByTitle(String title) {
        return (List<Book>) entityManager.createNativeQuery("SELECT * FROM book WHERE title LIKE CONCAT('%', ?1, '%')", Book.class)
                .setParameter(1, title)
                .getResultList();
    }

    @Override
    public int updateImageLink(String bookId, String imageLink) {
        return entityManager.createNativeQuery("UPDATE book SET image_link = ?1 WHERE id = ?2")
                .setParameter(1, imageLink)
                .setParameter(2, bookId)
                .executeUpdate();
    }

    public long readCount(String title) {
        return ((BigInteger) entityManager.createNativeQuery("SELECT COUNT(*) FROM book WHERE title LIKE CONCAT('%', ?1, '%')")
                .setParameter(1, title).getSingleResult()).longValue();
    }


}