package com.effectivesoft.bookservice.core.dao;

import com.effectivesoft.bookservice.core.model.Book;

import java.util.List;


public interface BookDao extends BaseDao<Book> {
    List<Book> readBooks(int limit, int offset, String sort);

    long readCount();

    long readCount(String title);

    List<Book> readBooksByTitle(String title);

    int updateImageLink(String bookId, String imageLink);
}
