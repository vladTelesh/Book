package com.effectivesoft.bookservice.core.dao;

import com.effectivesoft.bookservice.core.model.Author;
import com.effectivesoft.bookservice.core.model.Book;

import java.util.List;
import java.util.Optional;

public interface AuthorDao extends BaseDao<Author> {

    List<Author> readAuthors(String name, boolean generated, int limit, int offset, String sort);

    List<Book> readAuthorsBooks(String id, int limit, int offset);

    List<Author> readAuthorsByName(String name);

    Optional<Author> readAuthorByName(String name);

    long readAuthorsCount(String name, boolean generated);

    boolean updatePhotoLink(String authorId, String photoLink);
}
