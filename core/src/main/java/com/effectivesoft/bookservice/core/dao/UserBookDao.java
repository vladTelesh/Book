package com.effectivesoft.bookservice.core.dao;

import com.effectivesoft.bookservice.core.model.MonthlyUserStats;
import com.effectivesoft.bookservice.core.model.UserBook;
import com.effectivesoft.bookservice.core.model.AnnualUserStats;

import java.util.List;

public interface UserBookDao extends BaseDao<UserBook> {

    List<UserBook> readUserBooks(String id, int limit, int offset, String sort);

    long readCount(String userId);

    List<AnnualUserStats> readUserStats(String userId, List<Integer> years);

    List<MonthlyUserStats> readUserStats(String userId, int year);

    void deleteUserBook(String bookId, String userId);
}
