package com.effectivesoft.bookservice.rest.service;

import com.effectivesoft.bookservice.core.dao.UserBookDao;
import com.effectivesoft.bookservice.core.model.MonthlyUserStats;
import com.effectivesoft.bookservice.core.model.UserBook;
import com.effectivesoft.bookservice.common.dto.UserBookDto;
import com.effectivesoft.bookservice.core.model.AnnualUserStats;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserBookService {

    private final UserBookDao userBookDao;
    private final Mapper mapper;

    UserBookService(@Autowired UserBookDao bookDao,
                    @Autowired Mapper mapper) {
        this.userBookDao = bookDao;
        this.mapper = mapper;
    }

    @Transactional
    public Optional<UserBook> createUserBook(String bookId, String userId, UserBook userBook) {
        userBook.setBookId(bookId);
        userBook.setUserId(userId);
        userBook.setDateAdded(LocalDate.now());
        return userBookDao.create(userBook);
    }

    public List<UserBook> readUserBooks(String userId, int limit, int offset, String[] sortingColumns) {
        StringBuilder sort = new StringBuilder();
        if (sortingColumns != null) {
            for (String s : sortingColumns) {
                sort.append(s).append(",");
            }
            sort.deleteCharAt(sort.length() - 1);
        }
        List<UserBook> userBooks = userBookDao.readUserBooks(userId, limit, offset, sort.toString());
        return Objects.requireNonNullElse(userBooks, Collections.emptyList());
    }

    public long readUserBooksCount(String userId) {
        return userBookDao.readCount(userId);
    }

    public List<AnnualUserStats> readUserStats(String userId, int from, int to) {
        List<Integer> years = new ArrayList<>();
        for (int i = from; i <= to; i++) {
            years.add(i);
        }

        List<AnnualUserStats> annualUserStats = userBookDao.readUserStats(userId, years);

        if (annualUserStats.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> yearsWithoutStats = new ArrayList<>();

        int k = from;

        if (k != annualUserStats.get(0).getYear()) {
            while (k != annualUserStats.get(0).getYear()) {
                yearsWithoutStats.add(k);
                k++;
            }
        }

        for (int i = 0; i < annualUserStats.size(); i++) {
            if (i != annualUserStats.size() - 1) {
                int j = annualUserStats.get(i).getYear();
                while (j != annualUserStats.get(i + 1).getYear() - 1) {
                    yearsWithoutStats.add(j + 1);
                    j++;
                }
            }
        }

        k = to;

        if (k != annualUserStats.get(annualUserStats.size() - 1).getYear()) {
            while (k != annualUserStats.get(annualUserStats.size() - 1).getYear()) {
                yearsWithoutStats.add(k);
                k--;
            }
        }

        yearsWithoutStats.forEach(year -> annualUserStats.add(new AnnualUserStats(year, 0)));

        annualUserStats.sort(Comparator.comparing(AnnualUserStats::getYear));

        return annualUserStats;
    }

    public List<MonthlyUserStats> readUserStats(String id, int year) {
        List<MonthlyUserStats> monthlyUserStats = userBookDao.readUserStats(id, year);

        List<Integer> monthsWithoutStats = new ArrayList<>();

        if (monthlyUserStats.isEmpty()) {
            return Collections.emptyList();
        }

        for (int i = 0; i < monthlyUserStats.size(); i++) {
            if (i != monthlyUserStats.size() - 1) {
                int j = monthlyUserStats.get(i).getMonth();
                while (j != monthlyUserStats.get(i + 1).getMonth() - 1) {
                    monthsWithoutStats.add(j + 1);
                    j++;
                }
            }
        }

        int k = 1;
        while (k != monthlyUserStats.get(0).getMonth()) {
            monthsWithoutStats.add(k);
            k++;
        }

        k = 12;
        while (k != monthlyUserStats.get(monthlyUserStats.size() - 1).getMonth()) {
            monthsWithoutStats.add(k);
            k--;
        }

        for (Integer monthWithoutStats : monthsWithoutStats) {
            monthlyUserStats.add(new MonthlyUserStats(monthWithoutStats, 0));
        }

        monthlyUserStats.sort(Comparator.comparing(MonthlyUserStats::getMonth));

        return monthlyUserStats;
    }

    @Transactional
    public Optional<UserBook> updateUserBook(UserBookDto userBookDto, String userId) {
        UserBook userBook = mapper.map(userBookDto, UserBook.class);
        userBook.setId(userBookDto.getId());
        userBook.setBookId(userBookDto.getBookDto().getId());
        userBook.setUserId(userId);
        return userBookDao.update(userBook);
    }

    @Transactional
    public void deleteUserBook(String bookId, String userId) {
        userBookDao.deleteUserBook(bookId, userId);
    }
}
