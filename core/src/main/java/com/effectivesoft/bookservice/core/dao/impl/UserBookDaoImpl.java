package com.effectivesoft.bookservice.core.dao.impl;

import com.effectivesoft.bookservice.core.dao.AbstractDao;
import com.effectivesoft.bookservice.core.dao.UserBookDao;
import com.effectivesoft.bookservice.core.model.MonthlyUserStats;
import com.effectivesoft.bookservice.core.model.UserBook;
import com.effectivesoft.bookservice.core.model.AnnualUserStats;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@SuppressWarnings("unchecked")
public class UserBookDaoImpl extends AbstractDao<UserBook> implements UserBookDao {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    protected Class<UserBook> getEntityType() {
        return UserBook.class;
    }


    @Override
    public List<UserBook> readUserBooks(String userId, int limit, int offset, String sort) {
        StringBuilder query = new StringBuilder("SELECT * FROM user_book, book WHERE user_id = ?1 AND user_book.book_id = book.id");
        if (!sort.equals("")) {
            query.append(" ORDER BY ").append(sort);
        }
        return entityManager.createNativeQuery(query.toString(), UserBook.class)
                .setParameter(1, userId)
                .setMaxResults(limit)
                .setFirstResult(offset)
                .getResultList();
    }

    @Override
    public long readCount(String id) {
        return ((BigInteger) entityManager.createNativeQuery("SELECT COUNT(*) FROM user_book WHERE user_id = ?1")
                .setParameter(1, id)
                .getSingleResult()).longValue();
    }

    @Override
    public List<AnnualUserStats> readUserStats(String userId, List<Integer> years) {
        List<AnnualUserStats> annualUserStats = new ArrayList<>();

        try {
            List<Object[]> results = entityManager.createNativeQuery("select year(date_read) as year, " +
                    "count(*) as count from user_book where user_id = ?1 " +
                    "and year(date_read) in (?2) group by year(date_read) order by year(date_read)")
                    .setParameter(1, userId)
                    .setParameter(2, years)
                    .getResultList();

            results.forEach(record ->
                    annualUserStats.add(new AnnualUserStats((Integer) record[0], ((BigInteger) record[1]).intValue())));

            return annualUserStats;
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<MonthlyUserStats> readUserStats(String userId, int year) {
        List<MonthlyUserStats> monthlyUserStats = new ArrayList<>();

        try {
            List<Object[]> results = entityManager.createNativeQuery("select MONTH(date_read) as month, " +
                    "count(*) as count from user_book where user_id = ?1 " +
                    "and year(date_read) = ?2 group by MONTH(date_read) order by MONTH(date_read)")
                    .setParameter(1, userId)
                    .setParameter(2, year)
                    .getResultList();

            results.forEach(record ->
                    monthlyUserStats.add(new MonthlyUserStats((Integer) record[0], ((BigInteger) record[1]).intValue())));

            return monthlyUserStats;
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<UserBook> update(UserBook userBook) {
        entityManager.merge(userBook);
        return Optional.of(userBook);
    }

    @Override
    public void deleteUserBook(String bookId, String userId) {
        entityManager.createNativeQuery("DELETE FROM user_book WHERE book_id = ?1 AND user_id = ?2", UserBook.class)
                .setParameter(1, bookId)
                .setParameter(2, userId).executeUpdate();
    }
}
