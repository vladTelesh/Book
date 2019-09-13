package com.effectivesoft.bookservice.core.dao.impl;

import com.effectivesoft.bookservice.core.dao.AbstractDao;
import com.effectivesoft.bookservice.core.dao.AuthorDao;
import com.effectivesoft.bookservice.core.model.Author;
import com.effectivesoft.bookservice.core.model.Book;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public class AuthorDaoImpl extends AbstractDao<Author> implements AuthorDao {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    protected Class<Author> getEntityType() {
        return Author.class;
    }

    @Override
    public List<Author> readAuthors(String name, boolean generated, int limit, int offset, String sort) {
        StringBuilder query = new StringBuilder("SELECT * FROM author WHERE name LIKE CONCAT('%', ?1, '%')");

        if (name == null) {
            name = "";
        }

        if(generated){
            query.append(" AND is_generated = true");
        }

        if (!sort.equals("")) {
            query.append(" ORDER BY ").append(sort);
        }
        return entityManager.createNativeQuery(query.toString(), Author.class)
                .setParameter(1, name)
                .setMaxResults(limit)
                .setFirstResult(offset)
                .getResultList();
    }

    @Override
    public List<Book> readAuthorsBooks(String id, int limit, int offset) {
        return (List<Book>) entityManager.createNativeQuery("SELECT * FROM book WHERE author_id = ?1", Book.class)
                .setParameter(1, id)
                .setMaxResults(limit)
                .setFirstResult(offset)
                .getResultList();
    }

    @Override
    public List<Author> readAuthorsByName(String name) {
        return (List<Author>) entityManager.createNativeQuery("SELECT * FROM author WHERE name LIKE CONCAT('%', ?1, '%')", Author.class)
                .setParameter(1, name)
                .getResultList();
    }

    @Override
    public Optional<Author> readAuthorByName(String name) {
        try {
            return Optional.of((Author) entityManager.createNativeQuery("SELECT * FROM author WHERE name = ?1", Author.class)
                    .setParameter(1, name)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public long readAuthorsCount(String name, boolean generated) {
        String query = "SELECT COUNT(*) FROM author WHERE name LIKE CONCAT('%', ?1, '%')";
        if(generated){
            query += " AND is_generated = true";
        }

        return ((BigInteger) entityManager.createNativeQuery(query)
                .setParameter(1, name)
                .getSingleResult()).longValue();


    }

    @Override
    public boolean updatePhotoLink(String authorId, String photoLink) {
        int count = entityManager.createNativeQuery("UPDATE author SET photo_link = ?1 WHERE id = ?2")
                .setParameter(1, photoLink)
                .setParameter(2, authorId)
                .executeUpdate();
        return count == 1;
    }
}
